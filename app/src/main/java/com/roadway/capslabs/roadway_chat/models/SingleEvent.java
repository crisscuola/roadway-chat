package com.roadway.capslabs.roadway_chat.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 10.11.16
 */
public class SingleEvent {
    private final String title;
    private final String description;
    private final String address;
    private final String metro;
    private final DateRange range;
    private final int rating;
    private final int id;
    private final String pictureUrl;
    private final double let;
    private final double lng;
    private final String phone;
    private final String url;
    private final int countUsed;
    private final int color;
    private final String organization;

    public SingleEvent(JSONObject eventObj) {
        try {
            title = eventObj.getString("title");
            description = eventObj.getString("about");
            range = new DateRange(eventObj.getString("date_start"), eventObj.getString("date_end"));
            rating = (int) eventObj.getDouble("rating");
            id = eventObj.getInt("id");
            pictureUrl = eventObj.getString("avatar");
            address = eventObj.getString("address");
            metro = eventObj.getString("metro");
            let = eventObj.getDouble("latitude");
            lng = eventObj.getDouble("longitude");
            phone = eventObj.getString("phone");
            url = eventObj.getString("url");
            countUsed = eventObj.getInt("count_used");
            organization = eventObj.getString("organization_name");
            String line = eventObj.getString("line_number");
            if (line.equals("null")) {
                color = 0;
            } else color = eventObj.getInt("line_number");

            Log.d("color", String.valueOf(color));
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

    public String getAddress() {
        return address;
    }

    public String getMetro() {
        return metro;
    }

    public String getPhone() { return phone;}

    public String getUrl() { return url;}

    public int getCountUsed() {return countUsed;}

    public int getColor() {return  color;}

    public String getOrganization() {return  organization;}


}
