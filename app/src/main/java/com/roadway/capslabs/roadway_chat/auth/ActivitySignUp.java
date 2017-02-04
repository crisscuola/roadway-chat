package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivitySignUp extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty(messageResId = R.string.login_empty_message)
    @Email(messageResId = R.string.login_wrong_email_format)
    private EditText email;
    @NotEmpty(messageResId = R.string.login_empty_pass_message)
    @Password(min = 8, messageResId = R.string.login_wrong_pass_format)
    private EditText password1;
    @NotEmpty(messageResId = R.string.reg_empty_second_pass_message)
    @ConfirmPassword(messageResId = R.string.reg_second_pass_message)
    private EditText password2;

    private Button register;

    TextView errorsTextView;
    private final Activity context = this;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        register = (Button) findViewById(R.id.submit_register_button);
        errorsTextView = (TextView) findViewById(R.id.reg_errors);
        scrollView = (ScrollView) findViewById(R.id.scroll_signup);

        email.setTextColor(Color.BLACK);
        password1.setTextColor(Color.BLACK);
        password2.setTextColor(Color.BLACK);

        setListeners();
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

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password1.setTextColor(getResources().getColor(R.color.black));
                password1.setHintTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password2.setTextColor(getResources().getColor(R.color.black));
                password2.setHintTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @NonNull
    private RegisterForm readRegisterForm() {
        return new RegisterForm(
                email.getText().toString(),
                password1.getText().toString(),
                password2.getText().toString());
    }

    @Override
    public void onValidationSucceeded() {
        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }

        errorsTextView.setText("");
        dropEditTextColors();
        Registrator registrator = new RegistratorByEmail(readRegisterForm());
        new RegisterRequest().execute(registrator);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
        password1.setTextColor(getResources().getColor(R.color.black));
        password1.setHintTextColor(getResources().getColor(R.color.black));
        password2.setTextColor(getResources().getColor(R.color.black));
        password2.setHintTextColor(getResources().getColor(R.color.black));
    }

    private AlertDialog.Builder getAlert(final Activity context) {
        String title = getString(R.string.check_email_title);
        String message = getString(R.string.check_email_message);
        String okString = "OK";

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(okString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, ActivitySignIn.class);
                context.startActivity(intent);
            }
        });

        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });

        return ad;
    }

    private final class RegisterRequest extends AsyncTask<Object, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            register.setEnabled(false);
            register.setTextColor(getResources().getColor(R.color.l_9));
        }

        @Override
        protected String doInBackground(Object... params) {
            Registrator registrator = (RegistratorByEmail) params[0];

            return registrator.register();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_registration", result);
            register.setEnabled(true);
            register.setTextColor(getResources().getColor(R.color.black));
            JSONObject object;
            try {
                object = new JSONObject(result);

                if (object.has("errors")) {
                        JSONArray array = object.getJSONArray("errors");
                        for (int i = 0; i < array.length(); i++) {
                            int item = array.getInt(i);
                            if (item == 8) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.email_already_exists, Toast.LENGTH_SHORT).show();

                                return;
                            }
                        }

                    Toast.makeText(getApplicationContext(),
                            R.string.registration_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
            getAlert(context).show();
        }
    }
}