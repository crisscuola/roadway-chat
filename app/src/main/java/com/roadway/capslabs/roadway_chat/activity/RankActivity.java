package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.RatingVote;
import com.roadway.capslabs.roadway_chat.network.RatingVoteHandler;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

/**
 * Created by konstantin on 25.11.16
 */
public class RankActivity extends AppCompatActivity {
    private Activity context = this;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private RatingBar ratingBar;
    private Button buttonRank;
    private EditText review;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank);
        initViews("Rank");
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }

        Bundle data = getIntent().getExtras();
        final String subscription_id =  data.getString("subscription_id");

        buttonRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(context, "Please vote your visit", Toast.LENGTH_SHORT).show();
                    return;
                }
                RatingVote ratingVote = getRatingVote();
                new RateRequest().execute(ratingVote, subscription_id);
                Log.d("Rank", "Rank: " + String.valueOf(ratingVote.getStars()) + "  " + ratingVote.getText() + ", id=" + subscription_id);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initViews(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_restore);
        toolbar.setTitle(title);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        review = (EditText) findViewById(R.id.review);
        buttonRank = (Button) findViewById(R.id.rankButton);
    }

    private RatingVote getRatingVote() {
        return new RatingVote((int) ratingBar.getRating(), review.getText().toString());
    }

    private final class RateRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            RatingVote vote = (RatingVote) params[0];
            String id = (String) params[1];
            return new RatingVoteHandler().vote(context, vote, id);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT)
                    .show();
            Intent feedActivity = new Intent(context, FeedActivity.class);
            startActivity(feedActivity);
        }
    }
}