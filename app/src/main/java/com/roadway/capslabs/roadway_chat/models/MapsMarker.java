package com.roadway.capslabs.roadway_chat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by konstantin on 06.01.17.
 */
public class MapsMarker {
    private final String name;
    private final double lat;
    private final double lng;
    private final JSONArray event;
    private final int id;
    private final String adress;

    public MapsMarker(JSONObject markerObj) {

        try {
            name = markerObj.getString("name");
            adress = markerObj.getString("address");
            lat = markerObj.getDouble("latitude");
            lng = markerObj.getDouble("longitude");
            id = markerObj.getInt("id");
            event = markerObj.getJSONArray("events");

        } catch (JSONException e) {
            throw new RuntimeException("Error while parsing json", e);
        }
    }

    public String getName(){return name;}

    public double getLat(){return lat;}

    public double getLng(){return lng;}

    public int getId(){return id;}

    public String getAdress() {return adress;}

    public JSONArray getEvent(){return event;}


}
