package com.roadway.capslabs.roadway_chat.url;

import okhttp3.HttpUrl;

/**
 * Created by kirill on 19.09.16
 */
public class UrlFactory {
    public static HttpUrl getUrl(UrlType type) {
        return type.getUrl().build();
    }
}
