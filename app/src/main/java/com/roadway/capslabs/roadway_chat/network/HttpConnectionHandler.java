package com.roadway.capslabs.roadway_chat.network;

import com.roadway.capslabs.roadway_chat.url.UrlFactory;
import com.roadway.capslabs.roadway_chat.url.UrlType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlConst.URL;

/**
 * Created by konstantin on 11.09.16
 */
public class HttpConnectionHandler {
    private static final OkHttpClient client = new OkHttpClient();

    public static JSONObject getFeedStatus() {
        HttpUrl url = UrlFactory.getUrl(UrlType.VK_REGISTER);
        String result = execute(url, client);
        return parseJSON(result);
    }

    private static String execute(HttpUrl url, OkHttpClient client) {
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

    public static JSONObject parseJSON(String body) {
        JSONObject object;
        try {
            object = new JSONObject(body);
            return object;
        } catch (JSONException e) {
            throw new RuntimeException("Exception happened while parsing JSON from response: " + body, e);
        }
    }

    public static String executeCsrf(OkHttpClient client, CookieJar jar) {
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
        String data = "";

        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid).put("method", "publish");
            JSONObject item = new JSONObject();
            item.put("channel", "public:roadway").put("data", message);
            object.put("params", item);
            data = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType
                .parse("application/json"), data);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .post(body)
                .build();

        try {
            new OkHttpClient().newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " +
                    UrlType.API.getUrl(), e);
        }

    }

    public static String getCsrfToken() {
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
