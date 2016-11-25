package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;
import com.roadway.capslabs.roadway_chat.url.UrlType;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.LOGIN;
import static com.roadway.capslabs.roadway_chat.url.UrlType.LOGOUT;

/**
 * Created by kirill on 25.09.16
 */
public class LoginHelper {

    public String login(Activity context, String email, String password) {
        HttpUrl url = UrlFactory.getUrl(LOGIN);
        RequestBody formBody = formBody(email, password);
        Request request = buildRequest(url, formBody);

        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email",email).apply();

        return getResponse(context, request);
    }

    public String logout(Activity context) {
        HttpUrl url = UrlFactory.getUrl(LOGOUT);
        Request request = buildRequest(url, new FormBody.Builder().build());
        return getLogoutResponse(context, request);
    }

    private String getLogoutResponse(Activity context, Request request) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        try {
            Response response = client.newCall(request).execute();
            removeCookies(cookieJar);
            //clearCookies();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during logout request", e);
        }
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
            saveCookie(cookieJar);

            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }

    private void saveCookie(CookieJar cookieJar) {
        List<Cookie> cookies = cookieJar.loadForRequest(UrlType.LOGIN.getUrl().build());
        cookieJar.saveFromResponse(UrlType.FEED.getUrl().build(), cookies);
        cookieJar.saveFromResponse(UrlType.EVENT.getUrl().build(), cookies);
        cookieJar.saveFromResponse(UrlType.LOGOUT.getUrl().build(), cookies);
    }

    private void clearCookies() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        cookieManager.getCookieStore().removeAll();
    }

    private void removeCookies(CookieJar cookieJar) {
        List<Cookie> cookies = cookieJar.loadForRequest(UrlType.LOGOUT.getUrl().build());
        cookieJar.saveFromResponse(UrlType.FEED.getUrl().build(), cookies);
    }
}
