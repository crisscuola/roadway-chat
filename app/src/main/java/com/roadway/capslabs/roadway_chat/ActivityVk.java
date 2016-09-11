package com.roadway.capslabs.roadway_chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 08.09.16.
 */
public class ActivityVk extends AppCompatActivity {

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL,
            VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            VKSdk.login(this, scope);

    }


}
