package com.roadway.capslabs.roadway_chat.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.network.UrlConst.*;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    private final OkHttpClient client;

    public HttpConnectionHandler(OkHttpClient client) {
        this.client = client;
    }

    public String doGetRequest(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter(token, token).build();
        String result = execute(url);

        return result;
    }

    public String getProfile(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String getFeedStatus(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String logout(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String login(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    public String register(String token) {
        HttpUrl url = URL_BUILDER.addQueryParameter("login", token).build();
        String result = execute(url);

        return result;
    }

    private String execute(HttpUrl url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        String body;
        try {
            Response response = client.newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }

        return body;
    }

    private JSONObject parseJSON(String body) {
        JSONObject loginObject = null;
        try {
            loginObject = new JSONObject(body);
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + body, e);
        }
        return loginObject;
    }
}
