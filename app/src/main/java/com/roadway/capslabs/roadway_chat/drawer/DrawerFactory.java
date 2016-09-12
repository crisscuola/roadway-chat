package com.roadway.capslabs.roadway_chat.drawer;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roadway.capslabs.roadway_chat.R;

/**
 * Created by kirill on 12.09.16
 */
public class DrawerFactory {
    public DrawerBuilder getDrawerBuilder(Activity activity, Toolbar toolbar) {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.app_name);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.app_name);
        DrawerBuilder drawer = new DrawerBuilder()
            .withActivity(activity)
            .withToolbar(toolbar)
            .addDrawerItems(
                    item1,
                    new DividerDrawerItem(),
                    item2,
                    new SecondaryDrawerItem().withName(R.string.app_name)
            )
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Log.d("drawer click pos", String.valueOf(position));
                    return true;
                }
            });
        return drawer;
    }
}
