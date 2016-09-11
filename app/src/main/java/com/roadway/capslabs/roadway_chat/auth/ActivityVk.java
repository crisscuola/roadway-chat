package com.roadway.capslabs.roadway_chat.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by konstantin on 08.09.16.
 */
public class ActivityVk extends AppCompatActivity {

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL,
            VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};

    static public int MY_ID = 0;
    Button logout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);

        VKSdk.login(this, scope);

        if (VKAccessToken.currentToken() == null) {
            //nothing to do
            TextView name = (TextView) findViewById(R.id.user_name);
            name.setText("No login with Vk");

        } else {
            MY_ID = Integer.parseInt(VKSdk.getAccessToken().userId);

            Log.d("MY_ID", String.valueOf(MY_ID));


        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VKAccessToken.currentToken() == null) {
            //nothing to do
            TextView name = (TextView) findViewById(R.id.user_name);
            name.setText("No login with Vk");

        } else {

            MY_ID = Integer.parseInt(VKSdk.getAccessToken().userId);
            HttpConnectionHandler handler = new HttpConnectionHandler();
            TextView response = (TextView) findViewById(R.id.response);
            new SendToken().execute(handler, response);

            Log.d("token", String.valueOf(VKAccessToken.currentToken().accessToken));
            VKRequest request = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS, MY_ID,
                    VKApiConst.FIELDS, "photo_100", "first_name, last_name"));

            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    String first_name = " ";
                    String last_name = " ";

                    try {
                        JSONArray array = response.json.getJSONArray("response");
                        first_name = array.getJSONObject(0).getString("first_name");
                        last_name = array.getJSONObject(0).getString("last_name");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TextView name = (TextView) findViewById(R.id.user_name);
                    Log.d("isNull", String.valueOf(name == null));
                    Log.d("name", first_name);
                    name.setText(first_name + " " + last_name);

                    super.onComplete(response);
                }
            });
        }

        logout = (Button) findViewById(R.id.button_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.logout();
                TextView name = (TextView) findViewById(R.id.user_name);
                name.setText("No login with Vk");
                Intent lol = new Intent(view.getContext(), ActivityVk.class);
                startActivity(lol);

            }
        });

    }
    private class SendToken extends AsyncTask<Object, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Object[] params) {
            try {
                HttpConnectionHandler handler = (HttpConnectionHandler) params[0];
                Object[] objects = new Object[2];
                objects[0] = handler.doGetRequest(VKAccessToken.currentToken().accessToken);
                objects[1] = params[1];
                return objects;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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


