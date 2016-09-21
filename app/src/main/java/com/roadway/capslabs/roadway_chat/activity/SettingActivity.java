package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

/**
 * Created by konstantin on 18.09.16
 */
public class SettingActivity extends AppCompatActivity {

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory(new HttpConnectionHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolbar("Settings");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
    }
    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle(title);
    }

}
