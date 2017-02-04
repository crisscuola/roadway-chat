package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;

import org.json.JSONException;
import org.json.JSONObject;

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
//                if (emailSend.equals("")) {
//                    Log.d("forgot", "Send forgot null!");
//                } else {
                new RestoreRequest().execute(email.getText().toString());
//                    Log.d("forgot", "Send forgot!");
//                }
            }
        });

    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.btn_send);
        email.setTextColor(Color.BLACK);

    }


    private final class RestoreRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button.setEnabled(false);
            button.setTextColor(getResources().getColor(R.color.l_9));
        }

        @Override
        protected String doInBackground(Object... params) {
            LoginHelper helper = new LoginHelper();
            String email = (String) params[0];
//            String password= (String) params[1];

            return helper.forgot(context, email);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_login", result);
            button.setEnabled(true);
            button.setTextColor(getResources().getColor(R.color.black));
            JSONObject object;
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                throw new RuntimeException("Exception during json parsing", e);
            }
            if (object.has("errors")) {
                Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT)
                        .show();
                return;
            }


        }
    }
}
