package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.RegisterForm;
import com.roadway.capslabs.roadway_chat.network.registrator.Registrator;
import com.roadway.capslabs.roadway_chat.network.registrator.RegistratorByEmail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignUp extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    @Email
    private EditText email;
    @NotEmpty
    @Password(min = 8, message = "Password must contain numbers and letters, minimum length is 8")
    private EditText password1;
    @NotEmpty
    @ConfirmPassword
    private EditText password2;
    @NotEmpty
    private EditText firstName;
    @NotEmpty
    private EditText lastName;
    private Button register;

    private final Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();

        final Validator validator = new Validator(this);
        validator.setValidationListener(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
        register = (Button) findViewById(R.id.submit_register_button);
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

    @Override
    public void onValidationSucceeded() {
        Registrator registrator = new RegistratorByEmail(readRegisterForm());
        new RegisterRequest().execute(registrator);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
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
            JSONObject object;
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
            if (object.has("errors")) {
                Toast.makeText(getApplicationContext(),
                        "Registration failed", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, ConfirmRegistrationActivity.class);
            intent.putExtra("email", email.getText().toString());
            startActivity(intent);
        }
    }
}