package com.roadway.capslabs.roadway_chat;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 08.09.16.
 */
public class Application extends android.app.Application {
        @Override
        public void onCreate() {
               super.onCreate();

            VKSdk.initialize(this);
            FacebookSdk.sdkInitialize(this);
            AppEventsLogger.activateApp(this);
        }
}