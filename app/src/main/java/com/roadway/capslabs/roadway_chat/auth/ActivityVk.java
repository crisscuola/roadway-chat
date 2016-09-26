package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.concurrent.ExecutionException;

/**
 * Created by konstantin on 08.09.16
 */
public class ActivityVk extends AppCompatActivity {
    private String[] scope = new String[]{VKScope.EMAIL};
    private String status;
    private Activity context = this;

    private Toolbar toolbar;
    private final HttpConnectionHandler handler = new HttpConnectionHandler();
    private final DrawerFactory drawerFactory = new DrawerFactory(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);
        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(this, scope);
        } else {
            try {
                new RegisterRequest().execute(handler).get();
                if ("ok".equals(status)) {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, FeedActivity.class);
                    startActivity(intent);
                }
                //Log.d("status_status", status);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread was interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Exception while async task execution", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VKAccessToken.currentToken() != null) {
            try {
                new RegisterRequest().execute(handler).get();
                if ("ok".equals(status)) {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, FeedActivity.class);
                    startActivity(intent);
                }
                //Log.d("status_status", status);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread was interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Exception while async task execution", e);
            }
        }
    }

    private final class RegisterRequest extends AsyncTask<HttpConnectionHandler, Void, String> {
        @Override
        protected String doInBackground(HttpConnectionHandler... params) {
            return "string";//params[0].registerViaVk(context, VKAccessToken.currentToken().accessToken);
//            String user = "nope";
//            try {
//                user = (String) params[0].getWebSocketParams().get("user");
//                Log.d("user", user);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return user;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("status", result);
            status = result;
        }
    }
}


