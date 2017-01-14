package com.roadway.capslabs.roadway_chat.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by konstantin on 05.01.17.
 */
public class CustomClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private final int mId;

    public CustomClusterItem(double lat, double lng, String title, String snippet, int id) {
        mId = id;
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getSnippet(){
        return mSnippet;
    }

    public int getId() {return mId;}

}
