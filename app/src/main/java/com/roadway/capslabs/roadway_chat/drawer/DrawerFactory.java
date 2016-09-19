package com.roadway.capslabs.roadway_chat.drawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.login.LoginManager;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.activity.MapActivity;
import com.roadway.capslabs.roadway_chat.activity.ProfileActivity;
import com.roadway.capslabs.roadway_chat.activity.SettingActivity;
import com.roadway.capslabs.roadway_chat.auth.ActivitySignIn;
import com.vk.sdk.VKSdk;

/**
 * Created by kirill on 12.09.16
 */
public class DrawerFactory {
    public DrawerBuilder getDrawerBuilder(final Activity activity, Toolbar toolbar) {
        PrimaryDrawerItem feed = new PrimaryDrawerItem().withIdentifier(1).withName("Feed")
                .withBadge("19").withBadgeStyle(new BadgeStyle()
                        .withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));
        SecondaryDrawerItem map = new SecondaryDrawerItem().withIdentifier(2).withName("Map");
        SecondaryDrawerItem profile = new SecondaryDrawerItem().withIdentifier(2).withName("Profile");
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(2).withName("Settings");
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(2).withName("Logout");




        DrawerBuilder drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(getAccountHeader(activity))
                .addDrawerItems(
                        feed,
                        map,
                        profile,
                        settings,
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Class<? extends Activity> toActivity = getActivity(position);
                        Intent intent = new Intent(activity, toActivity);
                        if (position == 5) {
                            VKSdk.logout();
                            LoginManager.getInstance().logOut();
                        }

                        Log.d("pos", String.valueOf(position));
                        activity.startActivity(intent);
                        return true;
                    }
                });

        return drawer;
    }

    private AccountHeader getAccountHeader(Activity activity) {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(new ProfileDrawerItem().withName("Mike").withEmail("mikepenz@gmail.com").withTextColor(Color.BLACK))
                .build();

        return headerResult;
    }

    private Class<? extends Activity> getActivity(int i) {
        switch (i) {
            case 1:
                return FeedActivity.class;
            case 2:
                return MapActivity.class;
            case 3:
                return ProfileActivity.class;
            case 4:
                return SettingActivity.class;
            case 5:
                return ActivitySignIn.class;
            default:
                return FeedActivity.class;
        }
    }
}
