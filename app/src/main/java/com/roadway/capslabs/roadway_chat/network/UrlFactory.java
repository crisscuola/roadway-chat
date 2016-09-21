package com.roadway.capslabs.roadway_chat.network;

import okhttp3.HttpUrl;

/**
 * Created by kirill on 19.09.16
 */
public class UrlFactory {
    private final static String url = "www.p30710.lab1.stud.tech-mail.ru";
    private final static String PATH_REGISTER = "register";
    private final static String PATH_TOKEN = "token";
    private final static String PATH_CHAT = "chat";
    private final static String PATH_VK = "vk-oauth2";
    private final static String PATH_PARAMETERS = "parameters";
    private final static HttpUrl.Builder getTypeBuilder  = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_REGISTER)
            .addPathSegment(PATH_TOKEN);
    private final static HttpUrl.Builder postTypeBuilder  = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_REGISTER)
            .addPathSegment(PATH_TOKEN)
            .addPathSegment(PATH_VK);

    public static HttpUrl.Builder getRegisterUrl() {
        return postTypeBuilder;
    }
}
