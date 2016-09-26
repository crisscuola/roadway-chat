package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.CHAT;

/**
 * Created by kirill on 25.09.16
 */
public class ChatConnectionHandler {
    private final HttpConnectionHandler handler;

    public ChatConnectionHandler(HttpConnectionHandler handler) {
        this.handler = handler;
    }

    public JSONObject getChatParams(Activity context) {
        HttpUrl url = UrlFactory.getUrl(CHAT);
        Request request = buildRequest(url);
        String response = getResponse(context, request);
        Log.d("feed_body", response);
        return handler.parseJSON(response);
    }

    private Request buildRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    private String getResponse(Activity context, Request request) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();

            Log.d("status_chat",  cookieJar.loadForRequest(request.url()).get(1).toString());
            Log.d("status_chat",  result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
