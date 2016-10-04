package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.RegisterForm;
import com.roadway.capslabs.roadway_chat.network.registrator.Registrator;
import com.roadway.capslabs.roadway_chat.network.registrator.RegistratorByEmail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignUp extends AppCompatActivity {
    private EditText email;
    private EditText password1;
    private EditText password2;
    private EditText firstName;
    private EditText lastName;

    private Activity context = this;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = (EditText) findViewById(R.id.email);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);

        Button register = (Button) findViewById(R.id.submit_register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrator registrator = new RegistratorByEmail(readRegisterForm());
                new RegisterRequest().execute(registrator);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sign Up!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    @NonNull
    private RegisterForm readRegisterForm() {
        return new RegisterForm(
                email.getText().toString(),
                password1.getText().toString(),
                password2.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString());
    }

    private final class RegisterRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Registrator registrator = (RegistratorByEmail) params[0];
            //TODO: save user to sharedPrefs
            return registrator.register();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_registration", result);
            response = result;
            try {
                JSONObject object = new JSONObject(result);
                if (object.has("object")) {
                    Intent intent = new Intent(context, ConfirmRegistrationActivity.class);
                    intent.putExtra("email", email.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Registration failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}