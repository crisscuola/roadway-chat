package com.roadway.capslabs.roadway_chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

/**
 * Created by konstantin on 11.09.16
 */
public class ProfileActivity extends AppCompatActivity{

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory(new HttpConnectionHandler());
    private Button buttonWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar("Profile");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();


        buttonWrite = (Button) findViewById(R.id.btn_write);

        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SingleChatActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle(title);
    }
}
