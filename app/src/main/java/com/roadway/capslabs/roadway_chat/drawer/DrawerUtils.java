package com.roadway.capslabs.roadway_chat.drawer;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 20.09.16
 */
public class DrawerUtils {
    private static final long FEED_ITEM_ID = 1L;

    public static void updateBadge(Drawer drawer, HttpConnectionHandler handler) {
        drawer.updateBadge(FEED_ITEM_ID, new StringHolder(getCurrentFeedStatus(handler)));
    }

    private static String getCurrentFeedStatus(HttpConnectionHandler handler) {
        JSONObject currentFeedStatus = handler.getFeedStatus();
        try {
            return currentFeedStatus.getString("badge");
        } catch (JSONException e) {
            throw new RuntimeException("Exception while parsing json", e);
        }
    }
}
