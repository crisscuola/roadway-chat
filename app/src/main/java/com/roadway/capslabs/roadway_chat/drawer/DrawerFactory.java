package com.roadway.capslabs.roadway_chat.drawer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.FeedActivity;
import com.roadway.capslabs.roadway_chat.activity.MapsActivity;
import com.roadway.capslabs.roadway_chat.activity.OwnEventsActivity;
import com.roadway.capslabs.roadway_chat.activity.QrScannerActivity;
import com.roadway.capslabs.roadway_chat.activity.SubscribeEventsActivity;
import com.roadway.capslabs.roadway_chat.auth.ActivityAuth;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 12.09.16
 */
public class DrawerFactory {

    public DrawerBuilder getDrawerBuilder(final Activity activity, Toolbar toolbar) {
        DrawerBuilder drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(getAccountHeader(activity))
                .addDrawerItems(
                        getDrawerItems()
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Class<? extends Activity> toActivity = getActivity(position);
                        Intent intent = new Intent(activity, toActivity);

                        if (position == 4) {
                            //VKSdk.logout();
                            new Logouter().execute(activity);
                        }

                        activity.startActivity(intent);
                        return true;
                    }
                });

        return drawer;
    }

    private AccountHeader getAccountHeader(Activity activity) {
        try {
            JSONObject profile = getProfile();
            String name = (String) profile.get("name");
            String email = (String) profile.get("email");
            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(activity)
                   // .addProfiles(new ProfileDrawerItem())
                    .withTextColorRes(R.color.colorProfileName)
                    .withHeaderBackground(R.color.colorHeaderBackground)
                    .withSelectionListEnabledForSingleProfile(false)
                    .build();

            return headerResult;
        } catch (JSONException e) {
            throw new RuntimeException("Exception while parsing json", e);
        }
    }

    private IDrawerItem[] getDrawerItems() {
        List<IDrawerItem> items = new ArrayList<>();
        PrimaryDrawerItem events = new PrimaryDrawerItem().withIdentifier(1).withName("Feed");
        SecondaryDrawerItem map = new SecondaryDrawerItem().withIdentifier(2).withName("Map");
        SecondaryDrawerItem myDiscounts = new SecondaryDrawerItem().withIdentifier(7).withName("My discounts");
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(4).withName("Logout");
        items.add(events);
        items.add(map);
        items.add(myDiscounts);
        items.add(logout);
        IDrawerItem[] array = new IDrawerItem[items.size()];

        return items.toArray(array);
    }

    private Class<? extends Activity> getActivity(int i) {
        switch (i) {
            case 1:
                return FeedActivity.class;
            case 2:
                return MapsActivity.class;
            case 3:
                return SubscribeEventsActivity.class;
            case 4:
                return ActivityAuth.class;
            case 5:
                return QrScannerActivity.class;
            case 6:
                return OwnEventsActivity.class;
            case 7:
                return SubscribeEventsActivity.class;
            case 8:
                return ActivityAuth.class;
            default:
                return FeedActivity.class;
        }
    }

    private JSONObject getProfile() {
        try {
            return new JSONObject("{name:name, email:email}");
        } catch (JSONException e) {
            throw new RuntimeException("Exception while parsing json", e);
        }
    }

    private final class Logouter extends AsyncTask<Activity, Void, Activity> {
        @Override
        protected Activity doInBackground(Activity... params) {
            Activity context = params[0];
            new LoginHelper().logout(context);
            return params[0];
        }

        @Override
        protected void onPostExecute(Activity context) {
            super.onPostExecute(context);
            Intent intent = new Intent(context, ActivityAuth.class);
            context.startActivity(intent);
        }
    }
}
