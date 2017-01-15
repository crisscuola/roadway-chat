package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

/**
 * Created by kirill on 06.11.16
 */
public class EventDescriptionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_desctiption);
        initToolbar(getString(R.string.event_description_title));

        TextView description = (TextView) findViewById(R.id.description);
        String descriptionText = getIntent().getExtras().getString("description");
        description.setText(descriptionText);
    }

    private void initTool(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_no);
        toolbar.setTitle(title);
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_description);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        drawer =  drawerFactory.getDrawerBuilderWithout(this)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        onBackPressed();
                        return false;
                    }
                })
                .build();

        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
