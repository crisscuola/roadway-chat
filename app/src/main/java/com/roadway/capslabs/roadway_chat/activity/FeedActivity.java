package com.roadway.capslabs.roadway_chat.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.roadway.capslabs.roadway_chat.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.SingleDialogAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    private Button send;
    private EditText text;
    private ListView listView;

    private final List<ChatMessage> chatMessagesList = new ArrayList<>();

    private SingleDialogAdapter singleDialogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.feed_activity_title));
        drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();
        initViews();
        VKSdk.initialize(this);
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

                String msg = text.getText().toString();
                ChatMessage chatMessage = new ChatMessage(msg, true, null);

                singleDialogAdapter.add(chatMessage);
                singleDialogAdapter.notifyDataSetChanged();
            }
        });
    }

    private class DownloadingMessages extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return "buf";
        }

        @Override
        protected void onPostExecute(String result) {
            send.setText("Отправить");
            send.setClickable(true);
            Collections.reverse(chatMessagesList);
            singleDialogAdapter.copyArrayList(chatMessagesList);
            singleDialogAdapter.notifyDataSetChanged();
            chatMessagesList.clear();
        }

        @Override
        protected void onPreExecute() {
            send.setClickable(false);
            send.setText("Загрузка");
            listView.setAdapter(singleDialogAdapter);
        }
    }
}
