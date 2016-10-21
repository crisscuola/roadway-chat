package com.roadway.capslabs.roadway_chat.network.registrator;

import com.roadway.capslabs.roadway_chat.models.RegisterForm;
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
    private final RegisterForm registerForm;

    public RegistratorByEmail(RegisterForm registerForm) {
        this.registerForm = registerForm;
    }

    public String register() {
        HttpUrl url = UrlFactory.getUrl(REGISTER);
        //String csrfToken = HttpConnectionHandler.getCsrfToken();
        RequestBody formBody = formBody();
        Request request = buildRequest(url, formBody);
        return  getResponse(request);
    }

    private RequestBody formBody() {
        return new FormBody.Builder()
                .add("email", registerForm.getEmail())
                .add("password1", registerForm.getPassword1())
                .add("password2", registerForm.getPassword2())
                .add("first_name", registerForm.getFirstName())
                .add("last_name", registerForm.getLastName())
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody formBody) {
        return new Request.Builder()
                .url(url)
                //.addHeader("X-CSRFToken", csrfToken)
                .post(formBody)
                .build();
    }

    private String getResponse(Request request) {
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
