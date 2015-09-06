package com.sohu.kurento.netClient;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sohu.kurento.bean.RoomBean;
import com.sohu.kurento.bean.UserType;
import com.sohu.kurento.util.LogCat;
import com.sohu.kurento.util.LooperExecutor;

import org.webrtc.IceCandidate;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jingbiaowang on 2015/7/22.
 * <p>
 * 网络访问。
 */
public class KWWebSocketClient implements WebSocketChannel.WebSocketEvents, KWWebSocket {

    public interface OnListListener {
        void onListListener(ArrayList<RoomBean> masters);

        void onConnectionOpened();

        void onError(String msg);

        void onClose(String msg);
    }

    private WebSocketChannel webSocketChannel;
    private LooperExecutor executor;

    public void setListListener(OnListListener listListener) {
        this.listListener = listListener;
    }

    public void setEvent(KWEvent event) {
        this.event = event;
    }

    private static KWWebSocketClient instance;
    private KWEvent event;

    private OnListListener listListener;
    Gson gson = new GsonBuilder().create();

    public KWWebSocketClient() {
        webSocketChannel = new WebSocketChannel();

    }

    /**
     * @return
     */
    public static KWWebSocketClient init() {
        LogCat.v("KWWebSocketClient new instance");
        instance = new KWWebSocketClient();
        return instance;
    }

    public static KWWebSocketClient getInstance() {

        if (instance != null) {
            return instance;
        } else
            throw new NullPointerException("websocket client instance is null.");
    }

    public void setExecutor(LooperExecutor executor) {
        this.executor = executor;
        webSocketChannel.setExecutor(executor);
    }

    public void connect(String urlStr) {
        executor.requestStart();
        webSocketChannel.connect(urlStr, this);
    }

    @Override
    public void onError(String e) {

        event.portError(e);
    }

    @Override
    public void onConnected() {
        LogCat.debug("websocket connected...");
        if (listListener != null)
            listListener.onConnectionOpened();
    }

    @Override
    public void onMessage(String msg) {

        LogCat.debug("receive msg : " + msg);

        JsonObject jsonMsg = gson.fromJson(msg, JsonObject.class);
        String idStr = jsonMsg.get("id").getAsString();
        if (idStr != null && ((idStr.equals("masterResponse") || idStr.equals("viewerResponse")))) {
            onResponse(jsonMsg);
        } else if (idStr != null && "stopCommunication".equals(idStr)) {
            event.onDisconnect();
        } else if (idStr != null && "roomList".equals(idStr)) {
            onListResponse(jsonMsg);
        } else if (idStr != null && "register".equals(idStr)) {
            onRegister(jsonMsg);
        } else if (idStr != null && "iceCandidate".equals(idStr)) {

        }

    }

    /**
     * 服务器返回list。
     *
     * @param jsonMsg
     */
    private void onListResponse(JsonObject jsonMsg) {
        String reponseFlag = jsonMsg.get("response").getAsString();
        if (reponseFlag != null && reponseFlag.equals("accepted")) {

            Type collectionType = new TypeToken<ArrayList<RoomBean>>() {
            }.getType();
            ArrayList<RoomBean> masters = gson.fromJson(jsonMsg.get("roomsList").getAsJsonArray(), collectionType);
            listListener.onListListener(masters);
        } else {
            listListener.onError(jsonMsg.get("message").getAsString());
        }
    }

    /**
     * after register room from server.
     */
    private void onRegister(JsonObject jsonObject) {

        String response = jsonObject.get("response").toString();
        if (response != null && response.equals("accepted")) {
            event.onRegisterRoomSuccess(gson.fromJson(jsonObject.get("room"), RoomBean.class));
        } else {
            event.onRegisterRoomFailure(jsonObject.get("message").toString());
        }
    }


    private void onIceCandidate(JsonObject jsonObject) {

        JsonObject iceJson = jsonObject.getAsJsonObject("candidate");
        if (iceJson != null) {
            IceCandidate candidate = new IceCandidate(
                    iceJson.get("sdpMid").getAsString(),
                    iceJson.get("sdpMLineIndex").getAsInt(),
                    iceJson.get("candidate").getAsString());
            event.onRemoteIceCandidate(candidate);
        }
    }

    /**
     * 服务器返回数据。
     *
     * @param responseObj
     */

    private void onResponse(JsonObject responseObj) {
        if (responseObj.has("response") && "accepted".equals(responseObj.get("response").getAsString())) {
            event.onRemoteAnswer(responseObj.get("sdpAnswer").getAsString());
        } else {
            event.portError("'Call not accepted for the following reason: " + responseObj.get("message").getAsString());
        }
    }

    @Override
    public void onClosed(String msg) {
        if (listListener != null)
            listListener.onClose(msg);
        executor.requestStop();
    }

    private void sendStop() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String stopMsg = "{id:'stop'}";
                webSocketChannel.sendMsg(stopMsg);
            }
        });

        //TODO client stop.
    }

    public void disconnect() {
        sendStop();
    }

    public void close() {
        webSocketChannel.disconnect(true);
    }

    public void sendListerRequest() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JsonObject listRequest = new JsonObject();
                listRequest.addProperty("id", "roomList");
                webSocketChannel.sendMsg(listRequest.toString());
            }
        });
    }

    @Override
    public void sendMsg(final String msg) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //TODO package msg.
                webSocketChannel.sendMsg(msg);
            }
        });
    }

    @Override
    public void registerRoom(final String name) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", "register");
                jsonObject.addProperty("roomName", name);
                webSocketChannel.sendMsg(jsonObject.toString());
                LogCat.debug("register room : " + name);
            }
        });
    }

    /**
     * @param userType
     * @param sdp
     * @param roomName
     */
    public void sendSdp(final UserType userType, final String sdp, final String roomName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", userType.getVauleStr());
                jsonObject.addProperty("sdpOffer", sdp);
                if (userType == UserType.VIEWER) {
                    jsonObject.addProperty("roomName", roomName);
                }

                webSocketChannel.sendMsg(jsonObject.toString());
            }
        });
    }

    public class MyIceCondidate {
        private String candidate;
        private String sdpMid;
        private int sdpMLineIndex;

        public MyIceCondidate(String candidate, String sdpMid, int sdpMLineIndex) {
            this.candidate = candidate;
            this.sdpMid = sdpMid;
            this.sdpMLineIndex = sdpMLineIndex;
        }
    }

    @Override
    public void sendIceCandidate(final IceCandidate candidate) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", "onIceCandidate");
                jsonObject.add("candidate", gson.toJsonTree(new MyIceCondidate(candidate.sdp, candidate.sdpMid, candidate.sdpMLineIndex)));
                webSocketChannel.sendMsg(jsonObject.toString());
            }
        });
    }

}
