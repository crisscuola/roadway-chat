package com.roadway.capslabs.roadway_chat.network;

import okhttp3.HttpUrl;

/**
 * Created by kirill on 19.09.16
 */
public class UrlFactory {
    private final String url = "www.sermalenk.myjino.ru";
    private final String pathFirst = "dialog";
    private final String pathSecond = "key";
    private final static HttpUrl.Builder getTypeBuilder  = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(pathFirst)
            .addPathSegment(pathSecond);
    private final static HttpUrl.Builder postTypeBuilder  = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(pathFirst)
            .addPathSegment(pathSecond);

    public static HttpUrl.Builder getUrl(RequestType type) {
        if (type == RequestType.GET)
            return getTypeBuilder;
        return postTypeBuilder;
    }
}
