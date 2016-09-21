package com.roadway.capslabs.roadway_chat.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.network.UrlConst.*;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    public String doGetRequest(String token) {
        HttpUrl url = UrlFactory.getRegisterUrl().build();
        String result = execute(url);
        JSONObject object = parseJSON(result);
        return result;
    }

    public JSONObject getProfile(String profile) {
        HttpUrl url = UrlFactory.getRegisterUrl().build();
        String result = execute(url);
        JSONObject object = parseJSON(result);

        return object;
    }

    public JSONObject getFeedStatus(String token) {
        HttpUrl url = UrlFactory.getRegisterUrl().build();
        String result = execute(url);
        JSONObject object = parseJSON(result);
        return object;
    }

    public JSONObject getWebSocketParams(String token) {
        HttpUrl url = UrlFactory.getRegisterUrl().build();
        String result = execute(url);
        JSONObject object = parseJSON(result);
        return object;
    }

    public String logout(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String loginVk(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String registerViaVk(String token) {
        String response = doVkRegisterPostRequest(token);
        JSONObject object = parseJSON(response);
        String status = null;
        try {
            status = object.getString("status");
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + response, e);
        }
        return status;
    }

    private String execute(HttpUrl url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        String body;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new RuntimeException("Unexpected code " + response);

            body = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }

        return body;
    }

    private JSONObject parseJSON(String body) {
        JSONObject object;
        try {
            object = new JSONObject(body);
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + body, e);
        }

        return object;
    }

    private String doVkRegisterPostRequest(String token) {
        HttpUrl url = UrlFactory.getRegisterUrl().build();

        RequestBody formBody = new FormBody.Builder()
                .add("access_token", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            String result = response.body().string();
            Log.d("status_handler", result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }
    }
}
