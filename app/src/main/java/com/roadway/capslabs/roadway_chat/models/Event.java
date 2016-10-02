package com.roadway.capslabs.roadway_chat.models;

import java.util.List;

/**
 * Created by kirill on 02.10.16
 */
public class Event {
    private final String title;
    private final String description;
    private final User creator;
    private final int rating;
    private final Bonus bonus;

    public Event(String title, String description, User creator, int rating, Bonus bonus) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.rating = rating;
        this.bonus = bonus;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public int getRating() {
        return rating;
    }

    public Bonus getBonus() {
        return bonus;
    }
}
