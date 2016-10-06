package com.roadway.capslabs.roadway_chat.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 02.10.16
 */
public class Event {
    private String title;
    private final String description;
    private byte[] image;
    private final DateRange range;
    private final float rating;
    private int id;
    //private final User creator;
//    private final Bonus bonus;

    public Event(String title, String description, byte[] image, DateRange range, float rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.range = range;
        this.rating = rating;
    }

    public Event(JSONObject eventObj) {
        try {
            //title = eventObj.getString("title");
            description = eventObj.getString("about");
            range = new DateRange(eventObj.getString("date_start"), eventObj.getString("date_end"));
            rating = (float) eventObj.getDouble("rating");
            id = eventObj.getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException("Error while parsing json", e);
        }
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

    public float getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }
}
