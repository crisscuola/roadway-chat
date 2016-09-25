package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.User;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.Registrator;

import java.util.concurrent.ExecutionException;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignUp extends AppCompatActivity {

    private Button buttonSignUp;
    private EditText email;
    private EditText password1;
    private EditText password2;
    private EditText username;
    private EditText firstname;
    private EditText lastname;

    private Activity context = this;
    private String response;
    private Registrator registrator = new Registrator(new HttpConnectionHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = (EditText) findViewById(R.id.email);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        username = (EditText) findViewById(R.id.username);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);

        buttonSignUp = (Button) findViewById(R.id.btn_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(
                        email.getText().toString(),
                        firstname.getText().toString(),
                        lastname.getText().toString(),
                        username.getText().toString(),
                        password1.getText().toString(),
                        password2.getText().toString()
                );
                try {
                    new RegisterRequest().execute(registrator, user).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Thread was interrupted", e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("Exception while async task execution", e);
                }

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sign Up!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private final class RegisterRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Registrator registrator = (Registrator) params[0];
            User user = (User) params[1];

            return registrator.register(context, user);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_regular", result);
            response = result;
        }
    }
}