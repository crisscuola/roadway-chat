package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by konstantin on 06.11.16.
 */
public class CustomMarker {

    private final String title;
    private final String description;
    private final int id;

    public CustomMarker(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {return id;}
}
