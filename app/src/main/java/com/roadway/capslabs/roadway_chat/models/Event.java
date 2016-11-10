package com.roadway.capslabs.roadway_chat.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 02.10.16
 */
public class Event {
    private final String title;
    private final String description;
    private final String address;
    private final String metro;
    private final DateRange range;
    private final float rating;
    private final int id;
    private final double distance;
    private final String pictureUrl;
    private final double let;
    private final double lng;

    public Event(JSONObject eventObj) {
        try {
            title = eventObj.getString("title");
            description = eventObj.getString("about");
            range = new DateRange(eventObj.getString("date_start"), eventObj.getString("date_end"));
            rating = (float) eventObj.getDouble("rating");
            id = eventObj.getInt("id");
            pictureUrl = eventObj.getString("avatar");
            address = eventObj.getString("address");
            metro = eventObj.getString("metro");
            let = eventObj.getDouble("latitude");
            lng = eventObj.getDouble("longitude");
            double distDouble = eventObj.getDouble("distance");
            distance = (float) Math.round(distDouble * 10d) / 10d;
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

    public double getLet() {
        return let;
    }

    public double getLng() {
        return lng;
    }

    public double getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public String getMetro() {
        return metro;
    }
}
