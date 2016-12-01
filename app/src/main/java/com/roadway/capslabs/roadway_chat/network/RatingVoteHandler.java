package com.roadway.capslabs.roadway_chat.network;

import com.roadway.capslabs.roadway_chat.models.RatingVote;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;

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
    public String vote(RatingVote vote, String id) {
        HttpUrl url = UrlFactory.getUrl(VOTE);
        RequestBody formBody = formBody(vote, id);
        Request request = buildRequest(url, formBody);
        return  getResponse(request);
    }

    private RequestBody formBody(RatingVote vote, String id) {
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
