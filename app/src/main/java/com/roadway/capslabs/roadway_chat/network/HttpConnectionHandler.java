package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.JsonWriter;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;
import com.roadway.capslabs.roadway_chat.url.UrlType;
import com.vk.sdk.VKAccessToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlConst.*;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    private OkHttpClient client = new OkHttpClient();

    public JSONObject getProfile(String profile) {
        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        String result = execute(url, client);
        JSONObject object = parseJSON(result);

        return object;
    }

    public JSONObject getFeedStatus(String token) {
        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        String result = execute(url, client);
        JSONObject object = parseJSON(result);
        return object;
    }

    public String registerViaVk(Activity context, String token) {
        String response = doVkRegisterPostRequest(context, token);
        Log.d("response_viaVk", response);
        JSONObject object = parseJSON(response);
        String status;
        try {
            status = object.getString("status");
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + response, e);
        }
        return status;
    }

    private String execute(HttpUrl url, OkHttpClient client) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        String body;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new RuntimeException("Unexpected code " + response);

            body = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + URL, e);
        }

        return body;
    }

    public JSONObject parseJSON(String body) {
        JSONObject object;
        try {
            object = new JSONObject(body);
            return object;
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + body, e);
        }
    }

    private String doVkRegisterPostRequest(Activity context, String token) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        String csrf = executeCsrf(client, cookieJar);
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

    public String executeCsrf(OkHttpClient client, CookieJar jar) {
        Request request = new Request.Builder()
                .url(UrlFactory.getUrl(UrlType.CSRF))
                .build();

        String token;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new RuntimeException("Unexpected code " + response);

            token = jar.loadForRequest(UrlType.CSRF.getUrl().build()).get(0).value();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " +
                    UrlType.CSRF.getUrl(), e);
        }

        return token;
    }

    public static void sendMessage(String uid, String message) {
        HttpUrl url = UrlFactory.getUrl(UrlType.API);
        String data = "{\"channel\": \"public:roadway\", \"data\": " + "\"" + message + "\"" + "}";
        try {
            JSONObject object = new JSONObject(data);
            Log.d("feed_object", object.toString() + " " + data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = new FormBody.Builder()
                .add("uid", uid)
                .add("method", "publish")
                .add("params", data)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response execute = new OkHttpClient().newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " +
                    UrlType.API.getUrl(), e);
        }

    }
    public String getCsrfToken() {
//        CookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build();
//        return executeCsrf(client, cookieJar);
        HttpUrl url = UrlFactory.getUrl(UrlType.CSRF);
        String result = execute(url, client);
        JSONObject object = parseJSON(result);
        try {
            return object.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "no token";
    }
}
