package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.centrifugal.centrifuge.android.Centrifugo;
import com.centrifugal.centrifuge.android.credentials.Token;
import com.centrifugal.centrifuge.android.credentials.User;
import com.centrifugal.centrifuge.android.listener.ConnectionListener;
import com.centrifugal.centrifuge.android.listener.DataMessageListener;
import com.centrifugal.centrifuge.android.listener.JoinLeaveListener;
import com.centrifugal.centrifuge.android.listener.SubscriptionListener;
import com.centrifugal.centrifuge.android.message.DataMessage;
import com.centrifugal.centrifuge.android.message.presence.JoinMessage;
import com.centrifugal.centrifuge.android.message.presence.LeftMessage;
import com.centrifugal.centrifuge.android.subscription.SubscriptionRequest;
import com.roadway.capslabs.roadway_chat.adapters.EventsAdapter;
import com.roadway.capslabs.roadway_chat.models.ChatMessage;
import com.roadway.capslabs.roadway_chat.models.Event;

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
    private final String url;
    private String ws;
    private final EventsAdapter adapter;
    private final Activity context;

    public WebSocketHandler(Activity context, EventsAdapter adapter, JSONObject object) {
        chatChannel = object.optString("chat_channel");
        info = object.optString("info");
        user = object.optString("user");
        sockJS = object.optString("sockjs_endpoint");
        timestamp = object.optString("timestamp");
        ws = object.optString("ws_endpoint");
        ws = ws.replace("http", "ws");
        token = object.optString("token");
        url = object.optString("url");
        this.adapter = adapter;
        this.context = context;
    }

    public Thread connect() {
        centrifugo = buildConnection();
        subscribe();
        initConnectionListener();
        initSubscriptionListener();
        initNewMsgListener();
        initJoinLeaveListener();

        return new Thread(new Runnable() {
            @Override
            public void run() {
                centrifugo.connect();
            }
        });
    }

    public void disconnect() {
        centrifugo.disconnect();
    }

    public void subscribe() {
        centrifugo.subscribe(new SubscriptionRequest(chatChannel));
    }

    public void sendMessage(String message) {
        HttpConnectionHandler.sendMessage(user, message);
    }

    private Centrifugo buildConnection() {
        return new Centrifugo.Builder(ws)
                .setUser(new User(user, null))
                .setToken(new Token(token, timestamp))
                .build();
    }

    private void initConnectionListener() {
        centrifugo.setConnectionListener(new ConnectionListener() {
            @Override
            public void onWebSocketOpen() {
                Log.d("con_listener", "open");
            }

            @Override
            public void onConnected() {
                Log.d("con_listener", "connected");
            }

            @Override
            public void onDisconnected(int code, String reason, boolean remote) {
                Log.d("con_listener", "disconnected " + reason + " " + remote);
            }
        });
    }

    private void initSubscriptionListener() {
        centrifugo.setSubscriptionListener(new SubscriptionListener() {
            @Override
            public void onSubscribed(final String channelName) {
                Log.d("con_listener", "sub");
            }

            @Override
            public void onUnsubscribed(final String channelName) {
                Log.d("con_listener", "unsub");
            }

            @Override
            public void onSubscriptionError(final String channelName, final String error) {
                Log.d("con_listener", "sub_error");
            }
        });
    }

    private void initNewMsgListener() {
        centrifugo.setDataMessageListener(new DataMessageListener() {
            @Override
            public void onNewDataMessage(final DataMessage message) {
                Log.d("con_listener", "new_data_msg " + message.getData());
                boolean isOut = false;
                if (message.getUUID().equals(token)) {
                    isOut = true;
                }
                Log.d("feed_isOut", message.getUUID() + " my: " + token);
                ChatMessage chatMessage = new ChatMessage(message.getData(), isOut, null);
                adapter.add(chatMessage);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void initJoinLeaveListener() {
        centrifugo.setJoinLeaveListener(new JoinLeaveListener() {
            @Override
            public void onJoin(final JoinMessage joinMessage) {
                Log.d("con_listener", joinMessage.getUser() + " join");
            }

            @Override
            public void onLeave(final LeftMessage leftMessage) {
                Log.d("con_listener", leftMessage.getUser() + " left");
            }
        });
    }
}
