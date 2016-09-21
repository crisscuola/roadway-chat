package com.roadway.capslabs.roadway_chat.network;

import com.centrifugal.centrifuge.android.Centrifugo;
import com.centrifugal.centrifuge.android.credentials.Token;
import com.centrifugal.centrifuge.android.credentials.User;

import org.json.JSONObject;

/**
 * Created by kirill on 19.09.16
 */
public class WebSocketHandler {
    private Centrifugo centrifugo;
    private final String userId;
    private final String timestamp;
    private final String token;
    private final String centrifugoAddress;
    private final HttpConnectionHandler handler;

    public WebSocketHandler(HttpConnectionHandler handler) {
        this.handler = handler;
        JSONObject object = handler.getWebSocketParams("socket");
        userId = object.optString("userId");
        timestamp = object.optString("timestamp");
        token = object.optString("token");
        centrifugoAddress = object.optString("centrifugoWS");
    }

    public void connect() {
        centrifugo = buildConnection();
        centrifugo.connect();
    }

    public void disconnect() {
        centrifugo.disconnect();
    }

    public void login() {
    }

    public void logout() {
    }

    public void sendMessage() {
    }

    private Centrifugo buildConnection() {
        return new Centrifugo.Builder(centrifugoAddress)
                .setUser(new User(userId, null))
                .setToken(new Token(token, timestamp))
                .build();
    }
}
