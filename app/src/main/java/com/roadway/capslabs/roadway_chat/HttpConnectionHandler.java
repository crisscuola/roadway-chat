package com.roadway.capslabs.roadway_chat;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    final String URL = "www.sermalenk.myjino.ru";
    final String PATH_1 = "dialog";
    final String PATH_2 = "key";
    final String PATH_DELETE = "delete";
    OkHttpClient client;

    public HttpConnectionHandler(){
        client = new OkHttpClient();
    }

    public String doGetRequest(String token) throws IOException {

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
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
