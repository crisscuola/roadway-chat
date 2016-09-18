package com.roadway.capslabs.roadway_chat.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.SingleDialogAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Collections;

public class FeedActivity extends AppCompatActivity {

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    Button send;
    EditText text;
    ListView listView;

    ArrayList<ChatMessage> chatMessagesList= new ArrayList<>();

    SingleDialogAdapter singleDialogAdapter;

    private class DownloadingMessages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String buf = "buf";

            return buf;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initToolbar();
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        text = (EditText) findViewById(R.id.textmsg);
        listView = (ListView) findViewById(R.id.listmsg);
        send = (Button) findViewById(R.id.sendmsg);

        singleDialogAdapter = new SingleDialogAdapter(this);
        listView.setAdapter(singleDialogAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = text.getText().toString();
                ChatMessage chatMessage = new ChatMessage(msg, true, null);

                singleDialogAdapter.add(chatMessage);
                singleDialogAdapter.notifyDataSetChanged();
            }
        });

        VKSdk.initialize(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

}
