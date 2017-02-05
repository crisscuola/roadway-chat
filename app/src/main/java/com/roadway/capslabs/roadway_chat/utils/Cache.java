package com.roadway.capslabs.roadway_chat.utils;

import com.roadway.capslabs.roadway_chat.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 05.02.17
 */
public class Cache {
    private final static List<Event> feed = new ArrayList<>();

    public static void saveFeed(List<Event> feed) {
        Cache.feed.clear();
        Cache.feed.addAll(feed);
    }

    public static boolean isFeedEmpty() {
        return feed.isEmpty();
    }

    public static List<Event> getFeed() {
        return feed;
    }

    public static void clearFeed() {
        feed.clear();
    }
}
