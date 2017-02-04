package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;

/**
 * Created by konstantin on 04.02.17.
 */
public class ActivityForgot extends AppCompatActivity {

    private EditText email;
    private Button button;
    private final Activity context = this;
    private String emailSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot);
        initViews();

        emailSend = email.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("forgot", "Send forgot!");
                if (emailSend.equals("")) {
                    Log.d("forgot", "Send forgot null!");
                } else {
                    new LoginHelper().forgot(context, emailSend);
                    Log.d("forgot", "Send forgot!");
                }
            }
        });

    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.btn_send);
        email.setTextColor(Color.BLACK);

    }
}
