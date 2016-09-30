package com.roadway.capslabs.roadway_chat.network.registrator;

import android.app.Activity;
import android.util.Log;

import com.facebook.AccessToken;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;
import com.roadway.capslabs.roadway_chat.url.UrlType;
import com.vk.sdk.VKAccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlConst.URL;

/**
 * Created by kirill on 26.09.16
 */
public class RegistratorViaVk implements Registrator {
    private final HttpConnectionHandler handler;
    private final String token;

    public RegistratorViaVk(HttpConnectionHandler handler, String token) {
        this.handler = handler;
        this.token = token;
    }

    @Override
    public String register(Activity context) {
        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        String csrfToken = handler.getCsrfToken();
        RequestBody formBody = formBody(csrfToken);
        Request request = buildRequest(url, formBody, csrfToken);
        return getResponse(request);
    }

    private RequestBody formBody(String csrfToken) {
        Log.d("status_token", token);
        String data = "";
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", token);

            data = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public String registerViaVk(Activity context) {
        String response = doVkRegisterPostRequest(context);
        Log.d("response_viaVk", response);
        JSONObject object = handler.parseJSON(response);
        String status;
        try {
            status = object.getString("status");
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + response, e);
        }
        return status;
    }

    private String doVkRegisterPostRequest(Activity context) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        String csrf = handler.executeCsrf(client, cookieJar);
        Log.d("csrf_vk", csrf + " " + token + "\n email " + VKAccessToken.currentToken().email);

        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        RequestBody formBody = new FormBody.Builder()
                .add("access_token", token)
                .add("csrfmiddlewaretoken", csrf)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d("status_handler", result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }
    }
}
