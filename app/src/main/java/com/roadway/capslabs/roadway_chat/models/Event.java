package com.roadway.capslabs.roadway_chat.models;

import java.util.Date;

/**
 * Created by kirill on 02.10.16
 */
public class Event {
    private final String title;
    private final String description;
    private final byte[] image;
    private final DateRange range;
    //private final User creator;
//    private final int rating;
//    private final Bonus bonus;

    public Event(String title, String description, byte[] image, DateRange range) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.range = range;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }

    public String getDateStart() {
        return range.getDateStart();
    }

    public String getDateEnd() {
        return range.getDateEnd();
    }
}
