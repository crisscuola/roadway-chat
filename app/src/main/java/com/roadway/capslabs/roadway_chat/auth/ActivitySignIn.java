package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignUp, buttonSignIn, buttonVk, buttonFb;
    private String status;
    private Activity signIn;
    public String [] scope = new String[] {VKScope.EMAIL};

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signIn = this;

        final HttpConnectionHandler handler = new HttpConnectionHandler();

//        if (VKAccessToken.currentToken() != null) {
//            Intent intent = new Intent(this, FeedActivity.class);
//            startActivity(intent);
//        }

        //FacebookSdk.sdkInitialize(getApplicationContext());

        buttonSignUp = (Button) findViewById(R.id.btn_up);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activitySignUp = new Intent(view.getContext(), ActivitySignUp.class);
                startActivity(activitySignUp);
            }
        });

        buttonSignIn = (Button) findViewById(R.id.btn_in);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FeedActivity.class);
                startActivity(intent);
            }
        });

        buttonVk = (Button) findViewById(R.id.btn_vk);

        buttonVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityVk.class);
                startActivity(intent);
            }
        });

        buttonFb = (Button) findViewById(R.id.btn_fb);

        buttonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityFb.class);
                startActivity(intent);
            }
        });
    }
}
