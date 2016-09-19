package com.roadway.capslabs.roadway_chat.network;

import okhttp3.HttpUrl;

/**
 * Created by kirill on 19.09.16
 */
public class UrlConst {
    public static final String URL = "www.sermalenk.myjino.ru";
    public static final String PATH_1 = "dialog";
    public static final String PATH_2 = "key";
    public static final HttpUrl.Builder URL_BUILDER = new HttpUrl.Builder()
            .scheme("http")
            .host(URL)
            .addPathSegment(PATH_1)
            .addPathSegment(PATH_2);
}
