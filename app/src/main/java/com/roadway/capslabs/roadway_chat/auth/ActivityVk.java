package com.roadway.capslabs.roadway_chat.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.network.registrator.Registrator;
import com.roadway.capslabs.roadway_chat.network.registrator.RegistratorViaVk;
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

    private final HttpConnectionHandler handler = new HttpConnectionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);
        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(context, scope);
        } else {
            try {
                Registrator registrator = new RegistratorViaVk(context, VKAccessToken.currentToken().accessToken);
                Log.d("vk_token_email", VKAccessToken.currentToken().accessToken);
                new RegisterRequest().execute(registrator).get();
                if ("ok".equals(status)) {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
                }
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
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }
    }

    private final class RegisterRequest extends AsyncTask<Registrator, Void, String> {
        @Override
        protected String doInBackground(Registrator... params) {
            String result = params[0].register();
            Log.d("activity_vk_result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("status", result);
            status = result;
            if ("ok".equals(status)) {
                Intent intent = new Intent(context, FeedActivity.class);
                startActivity(intent);
            }
        }
    }
}


