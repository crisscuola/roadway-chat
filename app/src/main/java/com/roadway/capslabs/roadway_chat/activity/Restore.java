package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;

/**
 * Created by konstantin on 23.11.16.
 */
public class Restore extends AppCompatActivity {
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        initToolbar("Restores");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_restore);
        toolbar.setTitle(title);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }
}
