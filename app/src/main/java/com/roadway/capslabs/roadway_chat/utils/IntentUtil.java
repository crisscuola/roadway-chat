package com.roadway.capslabs.roadway_chat.utils;

import android.app.Activity;
import android.content.Intent;

import com.roadway.capslabs.roadway_chat.activity.AccessTokenActivity;

/**
 * Created by konstantin on 08.12.16.
 */
public class IntentUtil {

    private Activity activity;

    // constructor
    public IntentUtil(Activity activity) {
        this.activity = activity;
    }

    public void showAccessToken() {
        Intent i = new Intent(activity, AccessTokenActivity.class);
        activity.startActivity(i);
    }
}
