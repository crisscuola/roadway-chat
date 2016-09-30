package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.models.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.SingleDialogAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.ChatConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.WebSocketHandler;
import com.vk.sdk.VKSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;
    private final static List<ChatMessage> chatMessagesList;
    private WebSocketHandler webSocketHandler;
    private JSONObject object;

    private Toolbar toolbar;
    private Button send;
    private EditText text;
    private ListView listView;
    private Drawer drawer;

    private final Activity context = this;

    private SingleDialogAdapter singleDialogAdapter;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory(handler);
        chatMessagesList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.feed_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();
        initViews();
        VKSdk.initialize(this);

        new ConnectRequest().execute();
        //webSocketHandler = new WebSocketHandler(object);
    }

    @Override
    protected void onStop() {
        webSocketHandler.disconnect();
        super.onStop();
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_feed);
        toolbar.setTitle(title);
    }

    private void initAdapter() {
        listView = (ListView) findViewById(R.id.listmsg);
        singleDialogAdapter = new SingleDialogAdapter(this);
        listView.setAdapter(singleDialogAdapter);
    }

    private void initViews() {
        text = (EditText) findViewById(R.id.textmsg);
        send = (Button) findViewById(R.id.sendmsg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msg = text.getText().toString();
//                ChatMessage chatMessage = new ChatMessage(msg, true, null);
//                singleDialogAdapter.add(chatMessage);
//                singleDialogAdapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        webSocketHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    private final class ConnectRequest extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject chatParams = new ChatConnectionHandler(new HttpConnectionHandler()).getChatParams(context);
            Log.d("feed_body1", chatParams.toString());
//            webSocketHandler = new WebSocketHandler(chatParams);
//            webSocketHandler.connect().start();

            return chatParams;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            webSocketHandler = new WebSocketHandler(context, singleDialogAdapter, jsonObject);
            webSocketHandler.connect().start();
//            Log.d("feed_body1", "print1");
//            Log.d("feed_body2", jsonObject.toString());
            Log.d("feed_onpost", "onpost");
            object = jsonObject;
        }
    }
}
