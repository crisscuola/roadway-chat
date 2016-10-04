package com.roadway.capslabs.roadway_chat.network.registrator;

import android.util.Log;

import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.CONFIRM;

/**
 * Created by kirill on 04.10.16
 */
public class ConfirmationRegistrator implements Registrator {
    private final String email;
    private final String key;

    public ConfirmationRegistrator(String email, String key) {
        this.email = email;
        this.key = key;
    }

    @Override
    public String register() {
        HttpUrl url = UrlFactory.getUrl(CONFIRM);
        RequestBody formBody = formBody();
        Request request = buildRequest(url, formBody);
        return  getResponse(request);
    }
    private RequestBody formBody() {
        Log.d("response_confirm_params", email + " " + key);
        return new FormBody.Builder()
                .add("email", email)
                .add("key", key)
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody formBody) {
        return new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
    }

    private String getResponse(Request request) {
        try {
            Response response = new OkHttpClient().newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
