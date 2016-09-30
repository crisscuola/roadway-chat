package com.roadway.capslabs.roadway_chat.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.roadway.capslabs.roadway_chat.R;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignIn extends AppCompatActivity  {

    private Button buttonSignUp, buttonSignIn, buttonVk, buttonFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

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
                Intent intent = new Intent(view.getContext(), SignIn.class);
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
