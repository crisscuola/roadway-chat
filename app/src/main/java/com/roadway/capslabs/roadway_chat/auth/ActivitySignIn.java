package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kirill on 25.09.16
 */
public class ActivitySignIn extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty(messageResId = R.string.login_empty_message)
    @Email(messageResId = R.string.login_wrong_email_format)
    private EditText email;
    @NotEmpty(messageResId = R.string.login_empty_pass_message)
    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC,
            messageResId = R.string.login_wrong_pass_format)
    private EditText password;
    private Button button;
    TextView errorsTextView;
    private final Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initViews();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }


    @Override
    public void onValidationSucceeded() {
        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }

        dropEditTextColors();
        new LoginRequest().execute(email.getText().toString(), password.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        dropEditTextColors();


        StringBuilder sb = new StringBuilder();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            sb.append(message).append("\n");
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setTextColor(getResources().getColor(R.color.red));
                editText.setHintTextColor(getResources().getColor(R.color.red));
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
        errorsTextView.setText(sb.toString());
    }

    private void dropEditTextColors() {
        email.setTextColor(getResources().getColor(R.color.black));
        email.setHintTextColor(getResources().getColor(R.color.black));
        password.setTextColor(getResources().getColor(R.color.black));
        password.setHintTextColor(getResources().getColor(R.color.black));
    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.btn_in);
        errorsTextView = (TextView) findViewById(R.id.login_errors);
        email.setTextColor(Color.BLACK);
        password.setTextColor(Color.BLACK);
    }

    private final class LoginRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            LoginHelper helper = new LoginHelper();
            String email = (String) params[0];
            String password= (String) params[1];

            return helper.login(context, email, password);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_login", result);

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

            Intent feedActivity = new Intent(context, FeedActivity.class);
            feedActivity.putExtra("email", email.getText().toString());
            feedActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(feedActivity);
        }
    }
}
