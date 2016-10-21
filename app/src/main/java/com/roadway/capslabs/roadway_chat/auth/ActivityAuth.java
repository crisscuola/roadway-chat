package com.roadway.capslabs.roadway_chat.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.url.UrlType;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by konstantin on 07.09.16
 */
public class ActivityAuth extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (isLoggedIn()) {
            Intent activitySignUp = new Intent(this, FeedActivity.class);
            startActivity(activitySignUp);
        }

        //FacebookSdk.sdkInitialize(getApplicationContext());

        Button buttonSignUp = (Button) findViewById(R.id.submit_register_button);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activitySignUp = new Intent(view.getContext(), ActivitySignUp.class);
                startActivity(activitySignUp);
            }
        });

        Button buttonSignIn = (Button) findViewById(R.id.btn_in);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivitySignIn.class);
                startActivity(intent);
            }
        });

        Button buttonVk = (Button) findViewById(R.id.btn_vk);

        buttonVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityVk.class);
                startActivity(intent);
            }
        });
    }

    private boolean isLoggedIn() {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        HttpUrl url = UrlType.FEED.getUrl().build();
        List<Cookie> cookies = cookieJar.loadForRequest(url);
        for (Cookie cookie : cookies) {
            if("sessionid".equals(cookie.name()))
                return true;
        }

        return false;
    }
}
