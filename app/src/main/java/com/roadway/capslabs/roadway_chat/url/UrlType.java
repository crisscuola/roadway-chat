package com.roadway.capslabs.roadway_chat.url;

import okhttp3.HttpUrl;

import static com.roadway.capslabs.roadway_chat.url.UrlConst.*;

/**
 * Created by kirill on 25.09.16
 */
public enum UrlType {
    CHAT {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host(URL)
                    .addPathSegment(PATH_CHAT)
                    .addPathSegment(PATH_PARAMETERS);
        }
    },
    VK_REGISTER {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host(URL)
                    .addPathSegment(PATH_REGISTER)
                    .addPathSegment(PATH_TOKEN)
                    .addPathSegment(PATH_VK)
                    .addPathSegment("");
        }
    },
    REGISTER {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host(URL)
                    .addPathSegment(PATH_REGISTER)
                    .addPathSegment("");
        }
    },
    CSRF {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host(URL)
                    .addPathSegment(PATH_GET_TOKEN)
                    .addPathSegment("");
        }
    },
    LOGIN {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host(URL)
                    .addPathSegment(PATH_LOGIN)
                    .addPathSegment("");
        }
    },
    API {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme("http")
                    .host("p30720.lab1.stud.tech-mail.ru")
                    .addPathSegment(PATH_API)
                    .addPathSegment("");
        }
    };

    public abstract HttpUrl.Builder getUrl();
}
