package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.auth.ActivityAuth;
import com.roadway.capslabs.roadway_chat.gcm.QuickstartPreferences;
import com.roadway.capslabs.roadway_chat.gcm.RegistrationIntentService;
import com.roadway.capslabs.roadway_chat.url.UrlType;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by kirill on 23.11.16
 */
public class SplashActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int DELAY = 2000;
    private static final String TAG = "SplashActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;
    private Button again;
    private Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (!isOnline()) {
            showNoInternetMessage();

            setContentView(R.layout.no_internet);

            //mRegistrationProgressBar.setVisibility(View.INVISIBLE);

            again = (Button) findViewById(R.id.button_again);

            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SplashActivity.class);
                    startActivity(intent);
                }
            });

            return;
        }

//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, token);
        initReceiver();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isLoggedIn()) {
                    startFeedActivity();
                    finish();
                } else {
                    Intent authActivity = new Intent(SplashActivity.this, ActivityAuth.class);
                    startActivity(authActivity);
                }
            }
        }, DELAY);
    }

    private void startFeedActivity() {
        Intent feedActivity = new Intent(this, FeedActivity.class);
        startActivity(feedActivity);
    }

    private void initReceiver() {
        mRegistrationBroadcastReceiver = getReceiverInstance();
        registerReceiver();

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private BroadcastReceiver getReceiverInstance() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
    }

    private boolean isLoggedIn() {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        HttpUrl url = UrlType.FEED.getUrl().build();
        List<Cookie> cookies = cookieJar.loadForRequest(url);
        for (Cookie cookie : cookies) {
            if ("sessionid".equals(cookie.name())) {
                Log.d("response_auth_session", cookie.value());
                return true;
            }
        }
        return false;
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void showNoInternetMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_internet)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                }).show();
    }
}
