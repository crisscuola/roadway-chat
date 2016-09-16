package com.roadway.capslabs.roadway_chat.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    private final String URL = "www.sermalenk.myjino.ru";
    private final String PATH_1 = "dialog";
    private final String PATH_2 = "key";
    private final OkHttpClient client = new OkHttpClient();

    public String doGetRequest(String token) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(URL)
                .addPathSegment(PATH_1)
                .addPathSegment(PATH_2)
                .addQueryParameter("token", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        String textResponse;
        try {
            Response response = client.newCall(request).execute();
            textResponse = response.body().string();
        } catch (IOException e) {
            textResponse = "Bad connection";
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }
        return textResponse;
    }
}
