package com.roadway.capslabs.roadway_chat.network;

import com.centrifugal.centrifuge.android.Centrifugo;

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
    private Centrifugo centrifugo;

    public String doGetRequest(String token) {
        HttpUrl url = UrlFactory.getUrl(RequestType.GET)
                .addQueryParameter(token, token)
                .build();
        String result = execute(url);
        JSONObject object = parseJSON(result);

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

        OkHttpClient client = new OkHttpClient();
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
        JSONObject loginObject;
        try {
            loginObject = new JSONObject(body);
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + body, e);
        }

        return loginObject;
    }
}
