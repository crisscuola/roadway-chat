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

import static com.roadway.capslabs.roadway_chat.network.ActionType.*;
import static com.roadway.capslabs.roadway_chat.network.UrlConst.URL;

/**
 * Created by kirill on 25.09.16
 */
public class Registrator {
    private final HttpConnectionHandler handler;

    public Registrator(HttpConnectionHandler handler) {
        this.handler = handler;
    }

    public String register(Activity context, String token) {
        HttpUrl url = UrlFactory.getUrl(VK_REGISTER);
        return null;
    }

    public String register(Activity context, User user) {
//        CookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build();
//        String csrf = handler.executeCsrf(client, cookieJar);
        HttpUrl url = UrlFactory.getUrl(REGISTER);
        Log.d("register_url", url.url().toString());
        String csrfToken = handler.getCsrfToken();
        Log.d("register_csrf", csrfToken);
        RequestBody formBody = formBody(user, csrfToken);
        Request request = buildRequest(url, formBody, csrfToken);
        return getResponse(request);
    }

    private RequestBody formBody(User user, String csrfToken) {
        Log.d("status_reg_user", user.toString());
        return new FormBody.Builder()
                .add("email", user.getEmail())
                .add("first_name", user.getFirstName())
                .add("last_name", user.getLastName())
                .add("username", user.getUserName())
                .add("password1", user.getPassword1())
                .add("password2", user.getPassword2())
                .add("csrfmiddlewaretoken", csrfToken)
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody formBody, String csrfToken) {
        return new Request.Builder()
                .url(url)
                .addHeader("X-CSRFToken", csrfToken)
                .post(formBody)
                .build();
    }

    private String getResponse(Request request) {
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d("status_registration", result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
