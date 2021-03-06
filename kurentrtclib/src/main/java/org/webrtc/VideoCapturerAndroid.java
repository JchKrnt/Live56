/*
 * libjingle
 * Copyright 2015 Google Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.webrtc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.Surface;
import android.view.WindowManager;

import org.json.JSONException;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.Logging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// Android specific implementation of VideoCapturer.
// An instance of this class can be created by an application using
// VideoCapturerAndroid.create();
// This class extends VideoCapturer with a method to easily switch between the
// front and back camera. It also provides methods for enumerating valid device
// names.
//
// Threading notes: this class is called from C++ code, Android Camera callbacks, and possibly
// arbitrary Java threads. All public entry points are thread safe, and delegate the work to the
// camera thread. The internal *OnCameraThread() methods must check |camera| for null to check if
// the camera has been stopped.
@SuppressWarnings("deprecation")
public class VideoCapturerAndroid extends VideoCapturer implements PreviewCallback {
  private final static String TAG = "VideoCapturerAndroid";
  private final static int CAMERA_OBSERVER_PERIOD_MS = 5000;

  private Camera camera;  // Only non-null while capturing.
  private HandlerThread cameraThread;
  private final Handler cameraThreadHandler;
  // |cameraSurfaceTexture| is used with setPreviewTexture. Must be a member, see issue webrtc:5021.
  private SurfaceTexture cameraSurfaceTexture;
  private Context applicationContext;
  // Synchronization lock for |id|.
  private final Object cameraIdLock = new Object();
  private int id;
  private Camera.CameraInfo info;
  private int cameraGlTexture = 0;
  private final FramePool videoBuffers;
  // Remember the requested format in case we want to switch cameras.
  private int requestedWidth;
  private int requestedHeight;
  private int requestedFramerate;
  // The capture format will be the closest supported format to the requested format.
  private CaptureFormat captureFormat;
  private int cameraFramesCount;
  private int captureBuffersCount;
  private final Object pendingCameraSwitchLock = new Object();
  private volatile boolean pendingCameraSwitch;
  private CapturerObserver frameObserver = null;
  private CameraErrorHandler errorHandler = null;

  // Camera error callback.
  private final Camera.ErrorCallback cameraErrorCallback =
      new Camera.ErrorCallback() {
    @Override
    public void onError(int error, Camera camera) {
      String errorMessage;
      if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
        errorMessage = "Camera server died!";
      } else {
        errorMessage = "Camera error: " + error;
      }
      Logging.e(TAG, errorMessage);
      if (errorHandler != null) {
        errorHandler.onCameraError(errorMessage);
      }
    }
  };

  // Camera observer - monitors camera framerate and amount of available
  // camera buffers. Observer is excecuted on camera thread.
  private final Runnable cameraObserver = new Runnable() {
    @Override
    public void run() {
      int cameraFps = (cameraFramesCount * 1000 + CAMERA_OBSERVER_PERIOD_MS / 2)
          / CAMERA_OBSERVER_PERIOD_MS;
      double averageCaptureBuffersCount = 0;
      if (cameraFramesCount > 0) {
        averageCaptureBuffersCount =
            (double)captureBuffersCount / cameraFramesCount;
      }
      Logging.d(TAG, "Camera fps: " + cameraFps + ". CaptureBuffers: " +
          String.format("%.1f", averageCaptureBuffersCount) +
          ". Pending buffers: " + videoBuffers.pendingFramesTimeStamps());
      if (cameraFramesCount == 0) {
        Logging.e(TAG, "Camera freezed.");
        if (errorHandler != null) {
          errorHandler.onCameraError("Camera failure.");
        }
      } else {
        cameraFramesCount = 0;
        captureBuffersCount = 0;
        cameraThreadHandler.postDelayed(this, CAMERA_OBSERVER_PERIOD_MS);
      }
    }
  };

  // Camera error handler - invoked when camera stops receiving frames
  // or any camera exception happens on camera thread.
  public static interface CameraErrorHandler {
    public void onCameraError(String errorDescription);
  }

  // Camera switch handler - one of these functions are invoked with the result of switchCamera().
  // The callback may be called on an arbitrary thread.
  public interface CameraSwitchHandler {
    // Invoked on success. |isFrontCamera| is true if the new camera is front facing.
    void onCameraSwitchDone(boolean isFrontCamera);
    // Invoked on failure, e.g. camera is stopped or only one camera available.
    void onCameraSwitchError(String errorDescription);
  }

  public static VideoCapturerAndroid create(String name,
      CameraErrorHandler errorHandler) {
    VideoCapturer capturer = VideoCapturer.create(name);
    if (capturer != null) {
      VideoCapturerAndroid capturerAndroid = (VideoCapturerAndroid) capturer;
      capturerAndroid.errorHandler = errorHandler;
      return capturerAndroid;
    }
    return null;
  }

  // Switch camera to the next valid camera id. This can only be called while
  // the camera is running.
  public void switchCamera(final CameraSwitchHandler handler) {
    if (Camera.getNumberOfCameras() < 2) {
      if (handler != null) {
        handler.onCameraSwitchError("No camera to switch to.");
      }
      return;
    }
    synchronized (pendingCameraSwitchLock) {
      if (pendingCameraSwitch) {
        // Do not handle multiple camera switch request to avoid blocking
        // camera thread by handling too many switch request from a queue.
        Logging.w(TAG, "Ignoring camera switch request.");
        if (handler != null) {
          handler.onCameraSwitchError("Pending camera switch already in progress.");
        }
        return;
      }
      pendingCameraSwitch = true;
    }
    cameraThreadHandler.post(new Runnable() {
      @Override public void run() {
        if (camera == null) {
          if (handler != null) {
            handler.onCameraSwitchError("Camera is stopped.");
          }
          return;
        }
        switchCameraOnCameraThread();
        synchronized (pendingCameraSwitchLock) {
          pendingCameraSwitch = false;
        }
        if (handler != null) {
          handler.onCameraSwitchDone(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
      }
    });
  }

  // Requests a new output format from the video capturer. Captured frames
  // by the camera will be scaled/or dropped by the video capturer.
  // TODO(magjed/perkj): Document what this function does. Change name?
  public void onOutputFormatRequest(final int width, final int height, final int fps) {
    cameraThreadHandler.post(new Runnable() {
      @Override public void run() {
        onOutputFormatRequestOnCameraThread(width, height, fps);
      }
    });
  }

  // Reconfigure the camera to capture in a new format. This should only be called while the camera
  // is running.
  public void changeCaptureFormat(final int width, final int height, final int framerate) {
    cameraThreadHandler.post(new Runnable() {
      @Override public void run() {
        startPreviewOnCameraThread(width, height, framerate);
      }
    });
  }

  // Helper function to retrieve the current camera id synchronously. Note that the camera id might
  // change at any point by switchCamera() calls.
  private int getCurrentCameraId() {
    synchronized (cameraIdLock) {
      return id;
    }
  }

  public List<CaptureFormat> getSupportedFormats() {
    return CameraEnumerationAndroid.getSupportedFormats(getCurrentCameraId());
  }

  // Called from native code.
  private String getSupportedFormatsAsJson() throws JSONException {
    return CameraEnumerationAndroid.getSupportedFormatsAsJson(getCurrentCameraId());
  }

  private VideoCapturerAndroid() {
    Logging.d(TAG, "VideoCapturerAndroid");
    cameraThread = new HandlerThread(TAG);
    cameraThread.start();
    cameraThreadHandler = new Handler(cameraThread.getLooper());
    videoBuffers = new FramePool(cameraThread);
  }

  private void checkIsOnCameraThread() {
    if (Thread.currentThread() != cameraThread) {
      throw new IllegalStateException("Wrong thread");
    }
  }

  // Called by native code.
  // Initializes local variables for the camera named |deviceName|. If |deviceName| is empty, the
  // first available device is used in order to be compatible with the generic VideoCapturer class.
  boolean init(String deviceName) {
    Logging.d(TAG, "init: " + deviceName);
    if (deviceName == null)
      return false;

    if (deviceName.isEmpty()) {
      synchronized (cameraIdLock) {
        this.id = 0;
      }
      return true;
    } else {
      for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
        if (deviceName.equals(CameraEnumerationAndroid.getDeviceName(i))) {
          synchronized (cameraIdLock) {
            this.id = i;
          }
          return true;
        }
      }
    }
    return false;
  }

  // Called by native code to quit the camera thread. This needs to be done manually, otherwise the
  // thread and handler will not be garbage collected.
  private void release() {
    if (isReleased()) {
      throw new IllegalStateException("Already released");
    }
    cameraThreadHandler.post(new Runnable() {
      @Override
      public void run() {
        if (camera != null) {
          throw new IllegalStateException("Release called while camera is running");
        }
        if (videoBuffers.pendingFramesCount() != 0) {
          throw new IllegalStateException("Release called with pending frames left");
        }
      }
    });
    cameraThread.quitSafely();
    ThreadUtils.joinUninterruptibly(cameraThread);
    cameraThread = null;
  }

  // Used for testing purposes to check if release() has been called.
  public boolean isReleased() {
    return (cameraThread == null);
  }

  // Called by native code.
  //
  // Note that this actually opens the camera, and Camera callbacks run on the
  // thread that calls open(), so this is done on the CameraThread.
  void startCapture(
      final int width, final int height, final int framerate,
      final Context applicationContext, final CapturerObserver frameObserver) {
    Logging.d(TAG, "startCapture requested: " + width + "x" + height
        + "@" + framerate);
    if (applicationContext == null) {
      throw new RuntimeException("applicationContext not set.");
    }
    if (frameObserver == null) {
      throw new RuntimeException("frameObserver not set.");
    }
    cameraThreadHandler.post(new Runnable() {
      @Override public void run() {
        startCaptureOnCameraThread(width, height, framerate, frameObserver,
            applicationContext);
      }
    });
  }

  private void startCaptureOnCameraThread(
      int width, int height, int framerate, CapturerObserver frameObserver,
      Context applicationContext) {
    Throwable error = null;
    checkIsOnCameraThread();
    if (camera != null) {
      throw new RuntimeException("Camera has already been started.");
    }
    this.applicationContext = applicationContext;
    this.frameObserver = frameObserver;
    try {
      synchronized (cameraIdLock) {
        Logging.d(TAG, "Opening camera " + id);
        camera = Camera.open(id);
        info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
      }
      // No local renderer (we only care about onPreviewFrame() buffers, not a
      // directly-displayed UI element).  Camera won't capture without
      // setPreview{Texture,Display}, so we create a SurfaceTexture and hand
      // it over to Camera, but never listen for frame-ready callbacks,
      // and never call updateTexImage on it.
      try {
        cameraGlTexture = GlUtil.generateTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        cameraSurfaceTexture = new SurfaceTexture(cameraGlTexture);
        cameraSurfaceTexture.setOnFrameAvailableListener(null);
        camera.setPreviewTexture(cameraSurfaceTexture);
      } catch (IOException e) {
        Logging.e(TAG, "setPreviewTexture failed", error);
        throw new RuntimeException(e);
      }

      Logging.d(TAG, "Camera orientation: " + info.orientation +
          " .Device orientation: " + getDeviceOrientation());
      camera.setErrorCallback(cameraErrorCallback);
      startPreviewOnCameraThread(width, height, framerate);
      frameObserver.OnCapturerStarted(true);

      // Start camera observer.
      cameraFramesCount = 0;
      captureBuffersCount = 0;
      cameraThreadHandler.postDelayed(cameraObserver, CAMERA_OBSERVER_PERIOD_MS);
      return;
    } catch (RuntimeException e) {
      error = e;
    }
    Logging.e(TAG, "startCapture failed", error);
    stopCaptureOnCameraThread();
    frameObserver.OnCapturerStarted(false);
    if (errorHandler != null) {
      errorHandler.onCameraError("Camera can not be started.");
    }
    return;
  }

  // (Re)start preview with the closest supported format to |width| x |height| @ |framerate|.
  private void startPreviewOnCameraThread(int width, int height, int framerate) {
    checkIsOnCameraThread();
    Logging.d(
        TAG, "startPreviewOnCameraThread requested: " + width + "x" + height + "@" + framerate);
    if (camera == null) {
      Logging.e(TAG, "Calling startPreviewOnCameraThread on stopped camera.");
      return;
    }

    requestedWidth = width;
    requestedHeight = height;
    requestedFramerate = framerate;

    // Find closest supported format for |width| x |height| @ |framerate|.
    final Camera.Parameters parameters = camera.getParameters();
    final int[] range = CameraEnumerationAndroid.getFramerateRange(parameters, framerate * 1000);
    final Camera.Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(
        parameters.getSupportedPreviewSizes(), width, height);
    final CaptureFormat captureFormat = new CaptureFormat(
        previewSize.width, previewSize.height,
        range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
        range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

    // Check if we are already using this capture format, then we don't need to do anything.
    if (captureFormat.equals(this.captureFormat)) {
      return;
    }

    // Update camera parameters.
    Logging.d(TAG, "isVideoStabilizationSupported: " +
        parameters.isVideoStabilizationSupported());
    if (parameters.isVideoStabilizationSupported()) {
      parameters.setVideoStabilization(true);
    }
    // Note: setRecordingHint(true) actually decrease frame rate on N5.
    // parameters.setRecordingHint(true);
    if (captureFormat.maxFramerate > 0) {
      parameters.setPreviewFpsRange(captureFormat.minFramerate, captureFormat.maxFramerate);
    }
    parameters.setPreviewSize(captureFormat.width, captureFormat.height);
    parameters.setPreviewFormat(captureFormat.imageFormat);
    // Picture size is for taking pictures and not for preview/video, but we need to set it anyway
    // as a workaround for an aspect ratio problem on Nexus 7.
    final Camera.Size pictureSize = CameraEnumerationAndroid.getClosestSupportedSize(
        parameters.getSupportedPictureSizes(), width, height);
    parameters.setPictureSize(pictureSize.width, pictureSize.height);

    // Temporarily stop preview if it's already running.
    if (this.captureFormat != null) {
      camera.stopPreview();
      // Calling |setPreviewCallbackWithBuffer| with null should clear the internal camera buffer
      // queue, but sometimes we receive a frame with the old resolution after this call anyway.
      camera.setPreviewCallbackWithBuffer(null);
    }

    // (Re)start preview.
    Logging.d(TAG, "Start capturing: " + captureFormat);
    this.captureFormat = captureFormat;

    List<String> focusModes = parameters.getSupportedFocusModes();
    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
      parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    camera.setParameters(parameters);
    videoBuffers.queueCameraBuffers(captureFormat.frameSize(), camera);
    camera.setPreviewCallbackWithBuffer(this);
    camera.startPreview();
  }

  // Called by native code.  Returns true when camera is known to be stopped.
  void stopCapture() throws InterruptedException {
    Logging.d(TAG, "stopCapture");
    final CountDownLatch barrier = new CountDownLatch(1);
    cameraThreadHandler.post(new Runnable() {
        @Override public void run() {
          stopCaptureOnCameraThread();
          barrier.countDown();
        }
    });
    barrier.await();
    Logging.d(TAG, "stopCapture done");
  }

  private void stopCaptureOnCameraThread() {
    checkIsOnCameraThread();
    Logging.d(TAG, "stopCaptureOnCameraThread");
    if (camera == null) {
      Logging.e(TAG, "Calling stopCapture() for already stopped camera.");
      return;
    }

    cameraThreadHandler.removeCallbacks(cameraObserver);
    Logging.d(TAG, "Stop preview.");
    camera.stopPreview();
    camera.setPreviewCallbackWithBuffer(null);
    videoBuffers.stopReturnBuffersToCamera();
    captureFormat = null;

    if (cameraGlTexture != 0) {
      GLES20.glDeleteTextures(1, new int[] {cameraGlTexture}, 0);
      cameraGlTexture = 0;
    }
    Logging.d(TAG, "Release camera.");
    camera.release();
    camera = null;
    cameraSurfaceTexture.release();
    cameraSurfaceTexture = null;
  }

  private void switchCameraOnCameraThread() {
    checkIsOnCameraThread();
    Logging.d(TAG, "switchCameraOnCameraThread");
    stopCaptureOnCameraThread();
    synchronized (cameraIdLock) {
      id = (id + 1) % Camera.getNumberOfCameras();
    }
    startCaptureOnCameraThread(requestedWidth, requestedHeight, requestedFramerate, frameObserver,
        applicationContext);
    Logging.d(TAG, "switchCameraOnCameraThread done");
  }

  private void onOutputFormatRequestOnCameraThread(int width, int height, int fps) {
    checkIsOnCameraThread();
    if (camera == null) {
      Logging.e(TAG, "Calling onOutputFormatRequest() on stopped camera.");
      return;
    }
    Logging.d(TAG, "onOutputFormatRequestOnCameraThread: " + width + "x" + height +
        "@" + fps);
    frameObserver.OnOutputFormatRequest(width, height, fps);
  }

  public void returnBuffer(final long timeStamp) {
    cameraThreadHandler.post(new Runnable() {
      @Override public void run() {
        videoBuffers.returnBuffer(timeStamp);
      }
    });
  }

  private int getDeviceOrientation() {
    int orientation = 0;

    WindowManager wm = (WindowManager) applicationContext.getSystemService(
        Context.WINDOW_SERVICE);
    switch(wm.getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_90:
        orientation = 90;
        break;
      case Surface.ROTATION_180:
        orientation = 180;
        break;
      case Surface.ROTATION_270:
        orientation = 270;
        break;
      case Surface.ROTATION_0:
      default:
        orientation = 0;
        break;
    }
    return orientation;
  }

  // Called on cameraThread so must not "synchronized".
  @Override
  public void onPreviewFrame(byte[] data, Camera callbackCamera) {
    checkIsOnCameraThread();
    if (camera == null) {
      return;
    }
    if (camera != callbackCamera) {
      throw new RuntimeException("Unexpected camera in callback!");
    }

    final long captureTimeNs =
        TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());

    captureBuffersCount += videoBuffers.numCaptureBuffersAvailable();
    int rotation = getDeviceOrientation();
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
      rotation = 360 - rotation;
    }
    rotation = (info.orientation + rotation) % 360;
    // Mark the frame owning |data| as used.
    // Note that since data is directBuffer,
    // data.length >= videoBuffers.frameSize.
    if (videoBuffers.reserveByteBuffer(data, captureTimeNs)) {
      cameraFramesCount++;
      frameObserver.OnFrameCaptured(data, videoBuffers.frameSize, captureFormat.width,
          captureFormat.height, rotation, captureTimeNs);
    } else {
      Logging.w(TAG, "reserveByteBuffer failed - dropping frame.");
    }
  }

  // Class used for allocating and bookkeeping video frames. All buffers are
  // direct allocated so that they can be directly used from native code. This class is
  // not thread-safe, and enforces single thread use.
  private static class FramePool {
    // Thread that all calls should be made on.
    private final Thread thread;
    // Arbitrary queue depth.  Higher number means more memory allocated & held,
    // lower number means more sensitivity to processing time in the client (and
    // potentially stalling the capturer if it runs out of buffers to write to).
    private static final int numCaptureBuffers = 3;
    // This container tracks the buffers added as camera callback buffers. It is needed for finding
    // the corresponding ByteBuffer given a byte[].
    private final Map<byte[], ByteBuffer> queuedBuffers = new IdentityHashMap<byte[], ByteBuffer>();
    // This container tracks the frames that have been sent but not returned. It is needed for
    // keeping the buffers alive and for finding the corresponding ByteBuffer given a timestamp.
    private final Map<Long, ByteBuffer> pendingBuffers = new HashMap<Long, ByteBuffer>();
    private int frameSize = 0;
    private Camera camera;

    public FramePool(Thread thread) {
      this.thread = thread;
    }

    private void checkIsOnValidThread() {
      if (Thread.currentThread() != thread) {
        throw new IllegalStateException("Wrong thread");
      }
    }

    public int numCaptureBuffersAvailable() {
      checkIsOnValidThread();
      return queuedBuffers.size();
    }

    // Discards previous queued buffers and adds new callback buffers to camera.
    public void queueCameraBuffers(int frameSize, Camera camera) {
      checkIsOnValidThread();
      this.camera = camera;
      this.frameSize = frameSize;

      queuedBuffers.clear();
      for (int i = 0; i < numCaptureBuffers; ++i) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
        camera.addCallbackBuffer(buffer.array());
        queuedBuffers.put(buffer.array(), buffer);
      }
      Logging.d(TAG, "queueCameraBuffers enqueued " + numCaptureBuffers
          + " buffers of size " + frameSize + ".");
    }

    // Return number of pending frames that have not been returned.
    public int pendingFramesCount() {
      checkIsOnValidThread();
      return pendingBuffers.size();
    }

    public String pendingFramesTimeStamps() {
      checkIsOnValidThread();
      List<Long> timeStampsMs = new ArrayList<Long>();
      for (Long timeStampNs : pendingBuffers.keySet()) {
        timeStampsMs.add(TimeUnit.NANOSECONDS.toMillis(timeStampNs));
      }
      return timeStampsMs.toString();
    }

    public void stopReturnBuffersToCamera() {
      checkIsOnValidThread();
      this.camera = null;
      queuedBuffers.clear();
      // Frames in |pendingBuffers| need to be kept alive until they are returned.
      Logging.d(TAG, "stopReturnBuffersToCamera called."
            + (pendingBuffers.isEmpty() ?
                   " All buffers have been returned."
                   : " Pending buffers: " + pendingFramesTimeStamps() + "."));
    }

    public boolean reserveByteBuffer(byte[] data, long timeStamp) {
      checkIsOnValidThread();
      final ByteBuffer buffer = queuedBuffers.remove(data);
      if (buffer == null) {
        // Frames might be posted to |onPreviewFrame| with the previous format while changing
        // capture format in |startPreviewOnCameraThread|. Drop these old frames.
        Logging.w(TAG, "Received callback buffer from previous configuration with length: "
            + (data == null ? "null" : data.length));
        return false;
      }
      if (buffer.capacity() != frameSize) {
        throw new IllegalStateException("Callback buffer has unexpected frame size");
      }
      if (pendingBuffers.containsKey(timeStamp)) {
        Logging.e(TAG, "Timestamp already present in pending buffers - they need to be unique");
        return false;
      }
      pendingBuffers.put(timeStamp, buffer);
      if (queuedBuffers.isEmpty()) {
        Logging.v(TAG, "Camera is running out of capture buffers."
            + " Pending buffers: " + pendingFramesTimeStamps());
      }
      return true;
    }

    public void returnBuffer(long timeStamp) {
      checkIsOnValidThread();
      final ByteBuffer returnedFrame = pendingBuffers.remove(timeStamp);
      if (returnedFrame == null) {
        throw new RuntimeException("unknown data buffer with time stamp "
            + timeStamp + "returned?!?");
      }

      if (camera != null && returnedFrame.capacity() == frameSize) {
        camera.addCallbackBuffer(returnedFrame.array());
        if (queuedBuffers.isEmpty()) {
          Logging.v(TAG, "Frame returned when camera is running out of capture"
              + " buffers for TS " + TimeUnit.NANOSECONDS.toMillis(timeStamp));
        }
        queuedBuffers.put(returnedFrame.array(), returnedFrame);
        return;
      }

      if (returnedFrame.capacity() != frameSize) {
        Logging.d(TAG, "returnBuffer with time stamp "
            + TimeUnit.NANOSECONDS.toMillis(timeStamp)
            + " called with old frame size, " + returnedFrame.capacity() + ".");
        // Since this frame has the wrong size, don't requeue it. Frames with the correct size are
        // created in queueCameraBuffers so this must be an old buffer.
        return;
      }

      Logging.d(TAG, "returnBuffer with time stamp "
          + TimeUnit.NANOSECONDS.toMillis(timeStamp)
          + " called after camera has been stopped.");
    }
  }

  // Interface used for providing callbacks to an observer.
  interface CapturerObserver {
    // Notify if the camera have been started successfully or not.
    // Called on a Java thread owned by VideoCapturerAndroid.
    abstract void OnCapturerStarted(boolean success);

    // Delivers a captured frame. Called on a Java thread owned by
    // VideoCapturerAndroid.
    abstract void OnFrameCaptured(byte[] data, int length, int width, int height,
                                  int rotation, long timeStamp);

    // Requests an output format from the video capturer. Captured frames
    // by the camera will be scaled/or dropped by the video capturer.
    // Called on a Java thread owned by VideoCapturerAndroid.
    abstract void OnOutputFormatRequest(int width, int height, int fps);
  }

  // An implementation of CapturerObserver that forwards all calls from
  // Java to the C layer.
  static class NativeObserver implements CapturerObserver {
    private final long nativeCapturer;

    public NativeObserver(long nativeCapturer) {
      this.nativeCapturer = nativeCapturer;
    }

    @Override
    public void OnCapturerStarted(boolean success) {
      nativeCapturerStarted(nativeCapturer, success);
    }

    @Override
    public void OnFrameCaptured(byte[] data, int length, int width, int height,
        int rotation, long timeStamp) {
      nativeOnFrameCaptured(nativeCapturer, data, length, width, height, rotation, timeStamp);
    }

    @Override
    public void OnOutputFormatRequest(int width, int height, int fps) {
      nativeOnOutputFormatRequest(nativeCapturer, width, height, fps);
    }

    private native void nativeCapturerStarted(long nativeCapturer,
        boolean success);
    private native void nativeOnFrameCaptured(long nativeCapturer,
        byte[] data, int length, int width, int height, int rotation, long timeStamp);
    private native void nativeOnOutputFormatRequest(long nativeCapturer,
        int width, int height, int fps);
  }
}
