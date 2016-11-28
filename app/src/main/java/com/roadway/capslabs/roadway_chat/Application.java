package com.roadway.capslabs.roadway_chat;

import com.orm.SugarApp;
import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 08.09.16.
 */
public class Application extends SugarApp {
        @Override
        public void onCreate() {
               super.onCreate();

            VKSdk.initialize(this);
//            FacebookSdk.sdkInitialize(this);
//            AppEventsLogger.activateApp(this);
        }
}