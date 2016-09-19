package com.roadway.capslabs.roadway_chat.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import okhttp3.OkHttpClient;

/**
 * Created by konstantin on 08.09.16
 */
public class ActivityVk extends AppCompatActivity {
    private String[] scope = new String[]{VKScope.EMAIL};

    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private final HttpConnectionHandler handler = new HttpConnectionHandler(new OkHttpClient());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);

        if (VKAccessToken.currentToken() == null){
            VKSdk.login(this, scope);
        } else {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }

        //ActivityUtils.initToolbar(this, toolbar, "Feed");
        drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VKAccessToken.currentToken() != null) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }

    }

    private class SendToken extends AsyncTask<Object, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Object[] params) {
            HttpConnectionHandler handler = (HttpConnectionHandler) params[0];
            Object[] objects = new Object[2];
            String response = handler.doGetRequest(VKAccessToken.currentToken().accessToken);
            objects[0] = response;
            objects[1] = params[1];

            return objects;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            String response = (String) objects[0];
            TextView responseTextView = (TextView) objects[1];
            responseTextView.setText(response);
        }
    }
}


