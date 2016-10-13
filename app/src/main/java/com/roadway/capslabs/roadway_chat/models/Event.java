package com.roadway.capslabs.roadway_chat.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 02.10.16
 */
public class Event {
    private final String title;
    private final String description;
    private byte[] image;
    private final DateRange range;
    private final float rating;
    private final int id;
    private final String pictureUrl;
    private final double let;
    private final double lng;
    //private final User creator;
//    private final Bonus bonus;


    public Event(JSONObject eventObj) {
        try {
            title = eventObj.getString("title");
            description = eventObj.getString("about");
            range = new DateRange(eventObj.getString("date_start"), eventObj.getString("date_end"));
            rating = (float) eventObj.getDouble("rating");
            id = eventObj.getInt("id");
            pictureUrl = eventObj.getString("avatar");
//            let = -37.81319D;
//            lng = 144.96298D;
            let = eventObj.getDouble("latitude");
            lng = eventObj.getDouble("longitude");
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public double getLet() {return let;
    }

    public double getLng() {
        return lng;
    }
}
