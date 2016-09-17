package com.roadway.capslabs.roadway_chat.drawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.activity.MapActivity;
import com.roadway.capslabs.roadway_chat.activity.ProfileActivity;

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



        DrawerBuilder drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(getAccountHeader(activity))
                .addDrawerItems(
                        feed,
                        map,
                        profile,
                        settings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = new Intent(activity, getActivity(position));
                        activity.startActivity(intent);
                        return true;
                    }
                });

        return drawer;
    }

    private AccountHeader getAccountHeader(Activity activity) {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com"))
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
            default:
                return FeedActivity.class;
        }
    }
}
