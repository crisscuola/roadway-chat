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
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.Password;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.registrator.ConfirmationRegistrator;
import com.roadway.capslabs.roadway_chat.network.registrator.Registrator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 04.10.16
 */
public class ConfirmRegistrationActivity extends AppCompatActivity {
    @Password(scheme = Password.Scheme.NUMERIC, message = "Should be numeric")
    private EditText codeField;
    private final Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String email = getIntent().getExtras().getString("email");
        setContentView(R.layout.activity_comfirm);
        Button confirm = (Button) findViewById(R.id.submit_confirm_button);
        final EditText keyField = (EditText) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = keyField.getText().toString();
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
            try {
                JSONObject object = new JSONObject(result);
                if (object.has("errors")) {
                    Toast.makeText(getApplicationContext(),
                            "Confirmation failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, ActivitySignIn.class);
                startActivity(intent);
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}
