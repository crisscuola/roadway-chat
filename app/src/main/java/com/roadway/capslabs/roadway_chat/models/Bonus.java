package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by kirill on 02.10.16
 */
public class Bonus {
    private final int id;
    private final String title;
    private final String description;

    public Bonus(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
