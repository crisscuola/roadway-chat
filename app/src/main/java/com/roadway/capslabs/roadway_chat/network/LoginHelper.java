package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.models.User;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.network.ActionType.LOGIN;

/**
 * Created by kirill on 25.09.16
 */
public class LoginHelper {
    private final HttpConnectionHandler handler;

    public LoginHelper(HttpConnectionHandler handler) {
        this.handler = handler;
    }

    public String login(Activity context, String email, String password) {
        HttpUrl url = UrlFactory.getUrl(LOGIN);
        Log.d("login_url", url.url().toString());
        RequestBody formBody = formBody(email, password);
        Request request = buildRequest(url, formBody);
        return getResponse(context, request);
    }

    private RequestBody formBody(String email, String password) {
        return new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody formBody) {
        return new Request.Builder()
                .url(url)
                .post(formBody)
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
            cookieJar.saveFromResponse(ActionType.CHAT.getUrl().build(),
                    cookieJar.loadForRequest(ActionType.LOGIN.getUrl().build()));

            Log.d("status_login1",  cookieJar.loadForRequest(request.url()).get(0).toString());
            Log.d("status_login2",  cookieJar.loadForRequest(ActionType.CHAT.getUrl().build()).get(1).toString());
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
