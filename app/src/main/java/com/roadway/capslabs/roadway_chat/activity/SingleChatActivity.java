package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.models.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 11.09.16.
 */
public class SingleChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;
    private final static List<ChatMessage> chatMessagesList;
    private Drawer drawer;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory(handler);
        chatMessagesList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        initToolbar(getString(R.string.single_chat_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }
}
