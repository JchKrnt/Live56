package com.sohu.live56.view.main.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sohu.kurento.bean.RoomBean;
import com.sohu.kurento.bean.SettingsBean;
import com.sohu.kurento.bean.UserType;
import com.sohu.kurento.client.AppRTCAudioManager;
import com.sohu.kurento.client.KWRtcSession;
import com.sohu.kurento.netClient.KWEvent;
import com.sohu.kurento.netClient.KWWebSocketClient;
import com.sohu.kurento.util.AppRTCUtils;
import com.sohu.live56.R;
import com.sohu.live56.exception.UnhandledExceptionHandler;
import com.sohu.live56.util.LogCat;
import com.sohu.live56.view.BaseActivity;
import com.sohu.live56.view.main.square.SquareFrag;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by jingbiaowang on 2015/8/13.
 * <p/>
 * in charge of video player.
 */
public abstract class PlayerActivity extends BaseActivity implements KWEvent {

    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private FrameLayout contentView;
    private GLSurfaceView glviewcall;
    private FrameLayout playerfragcontent;

    private AppRTCAudioManager audioManager;
    private SettingsBean settingsBean;
    private VideoRendererGui.ScalingType scalingType;
    private VideoRenderer.Callbacks remoteRender;
    private VideoRenderer.Callbacks localRender;
    private KWWebSocketClient webSocketClient;
    private KWRtcSession kwRtcSession;

    //showing msg dialog dependent on the activity life.
    private boolean activityRunning = true;
    //only use at master case.Sending sdp dependent on whether master view prepared and peer prepared.
    private boolean peerPrepare = false;
    private boolean viewPrepare = false;

    @Override
    protected View onCreateView() {
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        contentView = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_palyer_layout, null);
        contentView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initialize(contentView);

        initKWParamet();

        webSocketClient = KWWebSocketClient.getInstance();
        webSocketClient.setEvent(this);

        scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
        VideoRendererGui.setView(glviewcall, new Runnable() {
            @Override
            public void run() {
                creatPeerConnectionFactory();
            }
        });

        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, true);

        audioManager = AppRTCAudioManager.create(this, new Runnable() {
                    // This method will be called each time the audio state (number and
                    // type of devices) has been changed.
                    @Override
                    public void run() {
                        onAudioManagerChangedState();
                    }
                }
        );
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        LogCat.debug("Initializing the audio manager...");
//        audioManager.init();

        onAddFragment(R.id.player_frag_content);
        LogCat.v("PlayerActivity onCreateView()");

//        initAudioManager();

        return contentView;
    }

    private void initKWParamet() {
        settingsBean = new SettingsBean();
        if (this instanceof ObserverActivity)
            settingsBean.setUserType(UserType.VIEWER);
        else if (this instanceof LiveActivity)
            settingsBean.setUserType(UserType.PRESENTER);
        settingsBean.setVideoCallEnable(true);
        settingsBean.setVideoWidth(0);
        settingsBean.setVideoHeight(0);
        settingsBean.setFps(15);
        settingsBean.setStartVidoBitrateValue(256);
        settingsBean.setVideoCode("VP8");
        settingsBean.setHwCodeEnable(true);
        settingsBean.setAudioBitrateValue(0);
        settingsBean.setAudioCode("OPUS");
        settingsBean.setCpuUsageDetection(true);
    }

    private void creatPeerConnectionFactory() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (kwRtcSession == null) {
                    kwRtcSession = KWRtcSession.getInstance();
                    PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
                    options.networkIgnoreMask = PeerConnectionFactory.Options.ADAPTER_TYPE_UNKNOWN;
                    kwRtcSession.setPeerConnectionFactoryOptions(options);
                    kwRtcSession.createPeerConnectionFactory(localRender, remoteRender, settingsBean, PlayerActivity.this, VideoRendererGui.getEGLContext(), PlayerActivity.this);
                }
                onConnectedToRoomInternal();
            }
        });
    }

    private void onConnectedToRoomInternal() {
        kwRtcSession.createPeerConnection();
        kwRtcSession.createOffer();
    }

    protected abstract void onAddFragment(int contentViewId);

    protected abstract void onVideoConnected();


    private void initialize(View view) {

        glviewcall = (GLSurfaceView) view.findViewById(R.id.glview_call);
        playerfragcontent = (FrameLayout) view.findViewById(R.id.player_frag_content);
    }


    /**
     * Create and audio manager that will take care of audio routing,
     * audio modes, audio device enumeration etc.
     */
    private void initAudioManager() {
        audioManager = AppRTCAudioManager.create(this, new Runnable() {
                    // This method will be called each time the audio state (number and
                    // type of devices) has been changed.
                    @Override
                    public void run() {
                        onAudioManagerChangedState();
                    }
                }
        );
        audioManager.init();
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
    }


    protected void popMsgDialog(String msgStr) {
        if (!activityRunning) {
            LogCat.debug("Critical error: " + msgStr);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(msgStr)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            disconnect();
                        }
                    }).create().show();
        }
    }

    private void disconnect() {

        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        if (kwRtcSession != null) {
            kwRtcSession.close();
            kwRtcSession = null;
        }

        if (audioManager != null) {
            audioManager.close();
            audioManager = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        AppRTCUtils.logDeviceInfo("wertc_device");
        glviewcall.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
        activityRunning = true;
        if (kwRtcSession != null)
            kwRtcSession.startVideoSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glviewcall.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityRunning = false;
        if (kwRtcSession != null) {
            kwRtcSession.stopVideoSource();
        }
        disconnect();
    }

    @Override
    public void portError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popMsgDialog(msg);
            }
        });
    }

    @Override
    public void onRemoteAnswer(final String sdp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kwRtcSession.processAnwser(sdp);
            }
        });
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
                finish();
            }
        });
    }


    @Override
    public void onMessage(String msg) {

    }

    private String localSdpStr;

    @Override
    public void onLocalSdp(final SessionDescription localsdp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                localSdpStr = localsdp.description;
                peerPrepare = true;
                sendSdp();
            }
        });
    }


    /**
     * send sdp to server after live present is prepared.
     */
    private void sendSdp() {

        if (this instanceof ObserverActivity) {  //viewer.
            webSocketClient.sendSdp(settingsBean.getUserType(), localSdpStr, getIntent().getStringExtra(SquareFrag.MASTER_KEY));
            LogCat.debug("send sdp on observer");
        } else {      //Live presenter.
            if (peerPrepare && viewPrepare) {
                webSocketClient.sendSdp(settingsBean.getUserType(), localSdpStr, getIntent().getStringExtra(SquareFrag.MASTER_KEY));
                LogCat.debug("send sdp on Master");
            }
        }

    }

    @Override
    public void onRegisterRoomSuccess(RoomBean room) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LiveEvent) PlayerActivity.this).onRegistSuccess();
            }
        });
        sendSdp();
    }

    @Override
    public void onRegisterRoomFailure(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LiveEvent) PlayerActivity.this).onResiterFailure(msg);
            }
        });
        kwRtcSession.close();
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {

        kwRtcSession.addRemoteCandidate(candidate);
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {

        webSocketClient.sendIceCandidate(candidate);
    }

    @Override
    public void onClientPrepareComplete() {

    }

    @Override
    public void onIceConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onVideoConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {

    }

    @Override
    public void onPeerConnectionClosed() {

    }

    protected void registerRoom(String titleStr) {
        LogCat.debug("----------registerRoom from service.");
        webSocketClient.registerRoom(titleStr);
    }

    /**
     * Live presenter activity.
     * <p/>
     * send sdp to server after live present view is prepared.
     */
    protected void onViewPrepared() {
        viewPrepare = true;
        sendSdp();
    }
}
