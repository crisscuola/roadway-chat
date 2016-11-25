package com.roadway.capslabs.roadway_chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;

/**
 * Created by konstantin on 25.11.16.
 */
public class RankActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private RatingBar ratingBar;
    private Button buttonRank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        initViews("Rank");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        buttonRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Rank", String.valueOf(ratingBar.getRating()));
            }
        });

    }


    public void initViews(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_restore);
        toolbar.setTitle(title);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        buttonRank = (Button) findViewById(R.id.rankButton);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }
}