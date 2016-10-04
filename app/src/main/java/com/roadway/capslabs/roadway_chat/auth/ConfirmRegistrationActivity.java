package com.roadway.capslabs.roadway_chat.auth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.registrator.ConfirmationRegistrator;
import com.roadway.capslabs.roadway_chat.network.registrator.Registrator;

/**
 * Created by kirill on 04.10.16
 */
public class ConfirmRegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comfirm);
        final Button confirm = (Button) findViewById(R.id.submit_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = confirm.getText().toString();
                Registrator registrator = new ConfirmationRegistrator(email, key);
                new ConfirmRequest().execute(registrator);
            }
        });
    }

    private final class ConfirmRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Registrator registrator = (ConfirmationRegistrator) params[0];

            return registrator.register();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_confirm", result);
        }
    }
}
