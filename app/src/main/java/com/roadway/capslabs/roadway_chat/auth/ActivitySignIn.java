package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by kirill on 25.09.16
 */
public class ActivitySignIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private String response;
    private Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.btn_in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new LoginRequest().execute(new LoginHelper(new HttpConnectionHandler()),
                            email.getText().toString(), password.getText().toString()).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Thread was interrupted", e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("Exception while async task execution", e);
                }
                Intent activitySignUp = new Intent(view.getContext(), FeedActivity.class);
                startActivity(activitySignUp);
            }
        });
    }

    private final class LoginRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            LoginHelper helper = (LoginHelper) params[0];
            String email = (String) params[1];
            String password= (String) params[2];

            return helper.login(context, email, password);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_login", result);
            response = result;
        }
    }
}
