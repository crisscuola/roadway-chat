package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;

/**
 * Created by konstantin on 11.09.16.
 */
public class ProfileActivity extends AppCompatActivity{

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }
}
