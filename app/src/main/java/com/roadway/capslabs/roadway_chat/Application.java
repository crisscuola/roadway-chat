package com.roadway.capslabs.roadway_chat;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.orm.SugarApp;
import com.vk.sdk.VKSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by konstantin on 08.09.16.
 */
public class Application extends SugarApp {
        @Override
        public void onCreate() {
               super.onCreate();

            VKSdk.initialize(this);
            FacebookSdk.sdkInitialize(this);
            AppEventsLogger.activateApp(this);
            printKeyHash();
        }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("co.coderiver.facebooklogin_sample", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("SHA: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}