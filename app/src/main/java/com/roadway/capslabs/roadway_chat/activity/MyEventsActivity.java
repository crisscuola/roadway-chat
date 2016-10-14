package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.EventsAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.ChatMessage;
import com.roadway.capslabs.roadway_chat.network.ChatConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.WebSocketHandler;
import com.vk.sdk.VKSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 02.10.16.
 */
public class MyEventsActivity extends AppCompatActivity {
    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;
    private final static List<ChatMessage> chatMessagesList;
    private WebSocketHandler webSocketHandler;

    private Toolbar toolbar;
    private EditText text;
    private Button send;
    private ListView listView;
    private Drawer drawer;

    private final Activity context = this;

    private EventsAdapter eventsAdapter;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory();
        chatMessagesList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.feed_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();
        //initViews();
        VKSdk.initialize(this);

        //new ConnectRequest().execute();
    }

    @Override
    protected void onStop() {
//        webSocketHandler.disconnect();
        super.onStop();
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_feed);
        toolbar.setTitle(title);
    }

    private void initAdapter() {
        listView = (ListView) findViewById(R.id.events_list);
        eventsAdapter = new EventsAdapter(this);
        listView.setAdapter(eventsAdapter);
    }

    private void initViews() {
//        text = (EditText) findViewById(R.id.textmsg);
//        send = (Button) findViewById(R.id.sendmsg);
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String msg = text.getText().toString();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        webSocketHandler.sendMessage(msg);
//                    }
//                }).start();
//                text.setText("");
//            }
//        });
    }

    private final class ConnectRequest extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            return new ChatConnectionHandler(new HttpConnectionHandler()).getChatParams(context);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //webSocketHandler = new WebSocketHandler(context, eventsAdapter, jsonObject);
            webSocketHandler.connect().start();
        }
    }
}
