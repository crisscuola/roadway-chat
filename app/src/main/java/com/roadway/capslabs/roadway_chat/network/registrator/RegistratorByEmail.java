package com.roadway.capslabs.roadway_chat.network.registrator;

import android.app.Activity;
import android.util.Log;

import com.roadway.capslabs.roadway_chat.models.RegisterForm;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.REGISTER;

/**
 * Created by kirill on 25.09.16
 */
public class RegistratorByEmail implements Registrator {

    private final HttpConnectionHandler handler;
    private final RegisterForm registerForm;

    public RegistratorByEmail(HttpConnectionHandler handler, RegisterForm registerForm) {
        this.handler = handler;
        this.registerForm = registerForm;
    }

    public String register(Activity context) {
//        CookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build();
//        String csrf = handler.executeCsrf(client, cookieJar);
        HttpUrl url = UrlFactory.getUrl(REGISTER);
        String csrfToken = handler.getCsrfToken();
        Log.d("register_csrf", csrfToken);
        RequestBody formBody = formBody(csrfToken);
        Request request = buildRequest(url, formBody, csrfToken);
        return getResponse(request);
    }

    private RequestBody formBody(String csrfToken) {
        Log.d("status_reg_user", registerForm.toString());
        return new FormBody.Builder()
                .add("email", registerForm.getEmail())
                .add("password1", registerForm.getPassword1())
                .add("password2", registerForm.getPassword2())
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
