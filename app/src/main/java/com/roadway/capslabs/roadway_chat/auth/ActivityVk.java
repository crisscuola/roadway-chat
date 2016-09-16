package com.roadway.capslabs.roadway_chat.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
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

/**
 * Created by konstantin on 08.09.16
 */
public class ActivityVk extends AppCompatActivity {
    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL,
            VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};

    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private final HttpConnectionHandler handler = new HttpConnectionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);
        VKSdk.login(this, scope);

        initToolbar();
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        Button logout = (Button) findViewById(R.id.button_logout);
        if (logout != null) {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.logout();
                    setNameText("No login with Vk");
                    Intent intent = new Intent(view.getContext(), ActivityVk.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VKAccessToken.currentToken() != null) {
            int myId = Integer.parseInt(VKAccessToken.currentToken().userId);
            TextView response = (TextView) findViewById(R.id.response);
            new SendToken().execute(handler, response);

            VKRequest request = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS, myId,
                    VKApiConst.FIELDS, "photo_100", "first_name, last_name"));

            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    try {
                        JSONArray array = response.json.getJSONArray("response");
                        String firstName = array.getJSONObject(0).getString("first_name");
                        String lastName = array.getJSONObject(0).getString("last_name");
                        setNameText(firstName + " " + lastName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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

    private void setNameText(String text) {
        TextView name = (TextView) findViewById(R.id.user_name);
        if (name != null) {
            name.setText(text);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }
}


