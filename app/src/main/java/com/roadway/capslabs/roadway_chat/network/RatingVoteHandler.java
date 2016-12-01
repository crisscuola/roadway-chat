package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.models.RatingVote;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.VOTE;

/**
 * Created by kirill on 28.11.16
 */
public class RatingVoteHandler {
    public String vote(Activity context, RatingVote vote, String id) {
        HttpUrl url = UrlFactory.getUrl(VOTE);
        RequestBody formBody = formBody(vote, id);
        Request request = buildRequest(url, formBody);
        return  getResponse(context, request);
    }

    private RequestBody formBody(RatingVote vote, String id) {
        Log.d("Rate", id + " " + vote.getStars() + " " + vote.getText());
        return new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("rate", String.valueOf(vote.getStars()))
                .add("response", vote.getText())
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
            String resp = response.body().string();
            Log.d("Rate_response", resp);
            return resp;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }

}
