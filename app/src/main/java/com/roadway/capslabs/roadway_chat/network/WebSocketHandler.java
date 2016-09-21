package com.roadway.capslabs.roadway_chat.network;

import com.centrifugal.centrifuge.android.Centrifugo;
import com.centrifugal.centrifuge.android.credentials.Token;
import com.centrifugal.centrifuge.android.credentials.User;
import com.centrifugal.centrifuge.android.subscription.SubscriptionRequest;

import org.json.JSONObject;

/**
 * Created by kirill on 19.09.16
 */
public class WebSocketHandler {
    private Centrifugo centrifugo;
    private final String user;
    private final String timestamp;
    private final String token;
    private final String chatChannel;
    private final String info;
    private final String sockJS;
    private final String ws;

    public WebSocketHandler(JSONObject object) {
        chatChannel = object.optString("chat_channel");
        info = object.optString("info");
        user = object.optString("user");
        sockJS = object.optString("sockjs_endpoint");
        timestamp = object.optString("timestamp");
        ws = object.optString("ws_endpoint");
        token = object.optString("token");
    }

    public void connect() {
        centrifugo = buildConnection();
        centrifugo.connect();
    }

    public void disconnect() {
        centrifugo.disconnect();
    }

    public void subscribe() {
        centrifugo.subscribe(new SubscriptionRequest(chatChannel));
    }

    public void sendMessage() {
    }

    private Centrifugo buildConnection() {
        return new Centrifugo.Builder(ws)
                .setUser(new User(user, null))
                .setToken(new Token(token, timestamp))
                .build();
    }
}
