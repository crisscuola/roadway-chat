package com.roadway.capslabs.roadway_chat.auth;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivityAuth extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ActivityCompat.requestPermissions(ActivityAuth.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        Button buttonSignUp = (Button) findViewById(R.id.submit_register_button);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activitySignUp = new Intent(view.getContext(), ActivitySignUp.class);
                startActivity(activitySignUp);
            }
        });

        Button buttonSignIn = (Button) findViewById(R.id.btn_in);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivitySignIn.class);
                startActivity(intent);
            }
        });

        Button buttonGuest = (Button) findViewById(R.id.btn_guest);


        buttonGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FeedActivity.class);
                intent.putExtra("email", getResources().getString(R.string.guest_menu));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
