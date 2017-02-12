package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by konstantin on 04.02.17
 */
public class ActivityForgot extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty(messageResId = R.string.login_empty_message)
    @Email(messageResId = R.string.login_wrong_email_format)
    private EditText email;
    private Button button;
    private final Activity context = this;
    private String emailSend;
    private TextView errorsTextView;
    private Button again;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot);
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

        emailSend = email.getText().toString();
    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.btn_send);
        errorsTextView = (TextView) findViewById(R.id.forgot_errors);
        email.setTextColor(Color.BLACK);

        setListeners();
    }

    @Override
    public void onValidationSucceeded() {
        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }
        errorsTextView.setText("");
        dropEditTextColors();
        new RestoreRequest().execute(email.getText().toString());
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
    }

    private void setListeners() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setTextColor(getResources().getColor(R.color.black));
                email.setHintTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                dropEditTextColors();
            }
        });
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

            if (result.equals("Timeout")) {
                Log.d("Time", "Timeout UnFavoriter");
                setContentView(R.layout.no_internet);
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ActivityForgot.class);

                        finish();
                        startActivity(intent);
                    }
                });
            } else {

                try {
                    object = new JSONObject(result);

                    if (object.has("errors")) {
                        JSONArray array = object.getJSONArray("errors");

                        for (int i = 0; i < array.length(); i++) {
                            int item = array.getInt(i);
                            if (item == 17) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.user_not_exist, Toast.LENGTH_LONG).show();

                                return;
                            }
                        }

                        Toast.makeText(getApplicationContext(),
                                R.string.restore_failed, Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(context, R.string.restore_mail, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, ActivitySignIn.class);

                        finish();
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    throw new RuntimeException("Exception during json parsing", e);
                }
            }
        }
    }
}
