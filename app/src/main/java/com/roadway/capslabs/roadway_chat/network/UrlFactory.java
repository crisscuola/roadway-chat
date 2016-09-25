package com.roadway.capslabs.roadway_chat.network;

import okhttp3.HttpUrl;

/**
 * Created by kirill on 19.09.16
 */
public class UrlFactory {
    private final static String url = "23.251.141.209";
    private final static String PATH_REGISTER = "register";
    private final static String PATH_TOKEN = "token";
    private final static String PATH_CHAT = "chat";
    private final static String PATH_VK = "vk-oauth2";
    private final static String PATH_PARAMETERS = "parameters";
    private final static String PATH_ADMIN = "admin";
    private final static HttpUrl.Builder chatParametersBuilder = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_CHAT)
            .addPathSegment(PATH_PARAMETERS);
    private final static HttpUrl.Builder postVkRegisterBuilder = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_REGISTER)
            .addPathSegment(PATH_TOKEN)
            .addPathSegment(PATH_VK)
            .addPathSegment("");
    private final static HttpUrl.Builder csrf = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_ADMIN)
            .addPathSegment("");

    private final static HttpUrl.Builder postRegisterBuilder = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(PATH_REGISTER)
            .addPathSegment("");

    public static HttpUrl.Builder getVkRegisterUrl() {
        return postVkRegisterBuilder;
    }

    public static HttpUrl.Builder getChatParametersUrl() {
        return chatParametersBuilder;
    }

    public static HttpUrl.Builder getCsrfUrl() {
        return csrf;
    }

    public static HttpUrl.Builder getRegisterUrl() {
        return postRegisterBuilder;
    }

    public static HttpUrl getUrl(ActionType type) {
        return type.getUrl().build();
    }
}
