package com.roadway.capslabs.roadway_chat.network;

import android.app.Activity;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.url.UrlFactory;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.roadway.capslabs.roadway_chat.url.UrlType.CREATE;
import static com.roadway.capslabs.roadway_chat.url.UrlType.EVENT;
import static com.roadway.capslabs.roadway_chat.url.UrlType.FEED;
import static com.roadway.capslabs.roadway_chat.url.UrlType.OWN;
import static com.roadway.capslabs.roadway_chat.url.UrlType.PROFILE;
import static com.roadway.capslabs.roadway_chat.url.UrlType.SUBS;
import static com.roadway.capslabs.roadway_chat.url.UrlType.SUBSCRIBE;
import static com.roadway.capslabs.roadway_chat.url.UrlType.UNSUBSCRIBE;

/**
 * Created by kirill on 05.10.16
 */
public class EventRequestHandler {
     public <T extends Activity> String getAllEvents(T context) {
        HttpUrl url = UrlFactory.getUrl(FEED);
        Request request = buildRequest(url);
        return getResponse(context, request);
     }

    public String getOwnEvents(Activity context) {
        HttpUrl url = UrlFactory.getUrl(OWN);
        Request request = buildRequest(url);
        return getResponse(context, request);
    }

    public String getSubsEvents(Activity context) {
        HttpUrl url = UrlFactory.getUrl(SUBS);
        Request request = buildRequest(url);
        return getResponse(context, request);
    }

    public String getEvent(Activity context, String id) {
        HttpUrl url = UrlFactory.getUrl(EVENT).newBuilder()
                .addQueryParameter("id", id).build();
        Request request = buildRequest(url);
        return getResponse(context, request);
    }

    public String createEvent(Activity context, Event event) {
        HttpUrl url = UrlFactory.getUrl(CREATE);
        RequestBody formBody = formBody(event);
        Request request = buildRequest(url, formBody);
        return getResponse(context, request);
    }

    public String subscribeEvent(Activity context, String id){
        HttpUrl url = UrlFactory.getUrl(SUBSCRIBE);
        RequestBody formBody = formSubscribeBody(id);
        Request request = buildRequest(url, formBody);
        return getResponse(context, request);
    }

    public String unsubscribeEvent(Activity context, String id){
        HttpUrl url = UrlFactory.getUrl(UNSUBSCRIBE);
        RequestBody formBody = formSubscribeBody(id);
        Request request = buildRequest(url, formBody);
        return getResponse(context, request);
    }

    public String getCreator(Activity context, String id) {
        HttpUrl url = UrlFactory.getUrl(PROFILE).newBuilder()
                .addQueryParameter("id", id).build();
        Request request = buildRequest(url);
        return getResponse(context, request);
    }

    private RequestBody formBody(Event event) {
        Log.d("lolo_end", event.getDateEnd());
        return new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("title", event.getTitle())
                .addFormDataPart("about", event.getDescription())
                .addFormDataPart("date_start", event.getDateStart())
                .addFormDataPart("date_end", event.getDateEnd())
                //.addFormDataPart("image","profile.png", RequestBody.create(MediaType.parse("image/png"), event.getImage()))
                .build();
    }

    private RequestBody formSubscribeBody(String id) {
        return new FormBody.Builder()
                .add("event", id)
                .build();
    }

    private Request buildRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    private Request buildRequest(HttpUrl url, RequestBody body) {
        return new Request.Builder()
                .url(url)
                .post(body)
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

            Log.d("response_create_handler", resp);
            return resp;
        } catch (IOException e) {
            throw new RuntimeException("Connectivity problem happened during request to " + request.url(), e);
        }
    }
}
