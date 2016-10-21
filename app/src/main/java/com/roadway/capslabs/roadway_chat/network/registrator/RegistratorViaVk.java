package com.roadway.capslabs.roadway_chat.network.registrator;

import android.app.Activity;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;
import com.roadway.capslabs.roadway_chat.url.UrlType;
import com.vk.sdk.VKAccessToken;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kirill on 26.09.16
 */
public class RegistratorViaVk implements Registrator {
    private final Activity context;
    private final String token;

    public RegistratorViaVk(Activity context, String token) {
        this.context = context;
        this.token = token;
    }

    @Override
    public String register() {
        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        String csrfToken = HttpConnectionHandler.getCsrfToken();
        RequestBody formBody = formBody(csrfToken);
        Request request = buildRequest(url, formBody, csrfToken);
        String response = getResponse(request);
        JSONObject object = HttpConnectionHandler.parseJSON(response);

        return object.optString("status");
    }

    private RequestBody formBody(String csrfToken) {
        return new FormBody.Builder()
                .add("access_token", token)
                .add("user_social_id", VKAccessToken.currentToken().userId)
                .add("email", VKAccessToken.currentToken().email)
                .add("csrfmiddlewaretoken", csrfToken)
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody formBody, String csrfToken) {
        return new Request.Builder()
                .url(url)
                .addHeader("X-CSRFToken", csrfToken)
                //.addHeader("Content-type", "application/json")
                .post(formBody)
                .build();
    }

    private String getResponse(Request request) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            cookieJar.saveFromResponse(UrlType.CHAT.getUrl().build(),
                    cookieJar.loadForRequest(UrlType.VK_REGISTER.getUrl().build()));

            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
