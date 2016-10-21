package com.roadway.capslabs.roadway_chat.models;

import java.util.List;

/**
 * Created by kirill on 02.10.16
 */
public class User {
    private final String username;
    private final int rating;
    private final List<Event> userEvents;
    private final List<Bonus> userBonuses;

    public User(String username, int rating, List<Event> userEvents, List<Bonus> userBonuses) {
        this.username = username;
        this.rating = rating;
        this.userEvents = userEvents;
        this.userBonuses = userBonuses;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public List<Event> getUserEvents() {
        return userEvents;
    }

    public List<Bonus> getUserBonuses() {
        return userBonuses;
    }
}
