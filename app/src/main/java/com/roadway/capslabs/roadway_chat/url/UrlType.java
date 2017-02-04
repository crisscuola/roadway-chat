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
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_CHAT)
                    .addPathSegment(PATH_PARAMETERS);
        }
    },
    VK_REGISTER {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
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
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_REGISTER)
                    .addPathSegment("");
        }
    },
    CONFIRM {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_REGISTER)
                    .addPathSegment(PATH_CONFIRM)
                    .addEncodedPathSegment("");

        }
    },
    CSRF {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_GET_TOKEN)
                    .addPathSegment("");
        }
    },
    LOGIN {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_LOGIN)
                    .addPathSegment("");
        }
    },
    LOGOUT {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_LOGOUT)
                    .addPathSegment("");
        }
    },
    FORGOT {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_FORGOT)
                    .addPathSegment("");
        }
    },
    FEED {
        @Override
        public HttpUrl.Builder getUrl() {
            return EVENT.getUrl()
                    .addPathSegment("all")
                    .addPathSegment("");
        }
    },

    MAP {
        @Override
        public HttpUrl.Builder getUrl() {
            return EVENT.getUrl()
                    .addPathSegment("map")
                    .addPathSegment("");
        }
    },

    OWN {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_GET)
                    .addPathSegment(PATH_OWN)
                    .addPathSegment("");
        }
    },
    SUBS {
        @Override
        public  HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_GET)
                    .addPathSegment(PATH_SUBS)
                    .addPathSegment("");

        }
    },
    SUBSCRIBE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_SUBSCRIBE)
                    .addPathSegment("");
        }
    },
    UNSUBSCRIBE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_UNSUBSCRIBE)
                    .addPathSegment("");
        }
    },

    FAVOR {
        @Override
        public  HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_FAVOR)
                    .addPathSegment(PATH_GET)
                    .addPathSegment("");

        }
    },

    RECOMMENDED {
        @Override
        public  HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_GET)
                    .addPathSegment(PATH_RECOMMENDED)
                    .addPathSegment("");

        }
    },

    FAVORITE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_FAVORITE)
                    .addPathSegment("");
        }
    },

    UNFAVORITE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_UNFAVORITE)
                    .addPathSegment("");
        }
    },

    API {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL_CENTRIFUGE)
                    .addPathSegment(PATH_API)
                    .addPathSegment("");
        }
    },
    CREATE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_CREATE)
                    .addPathSegment("");
        }
    },
    EVENT {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(PATH_GET)
                    .addPathSegment("");
        }
    },
    PROFILE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_PROFILE)
                    .addPathSegment(PATH_GET)
                    .addPathSegment("");
        }
    },
    VOTE {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(RESPONSE)
                    .addPathSegment("");
        }
    },
    CONFIRMED {
        @Override
        public HttpUrl.Builder getUrl() {
            return new HttpUrl.Builder()
                    .scheme(HTTP)
                    .host(URL)
                    .addPathSegment(PATH_EVENT)
                    .addPathSegment(CONFIRMED_RESPONSES)
                    .addPathSegment("");
        }
    };

    public abstract HttpUrl.Builder getUrl();
}
