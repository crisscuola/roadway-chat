package com.roadway.capslabs.roadway_chat;

/**
 * Created by konstantin on 06.11.16.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MarkerAdapter implements InfoWindowAdapter {
    LayoutInflater inflater = null;
    private TextView textViewTitle, description;

    public MarkerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = inflater.inflate(R.layout.marker_window, null);
        if (marker != null) {
            textViewTitle = (TextView) v.findViewById(R.id.marker_title);
            description = (TextView) v.findViewById(R.id.marker_despription);

            textViewTitle.setText(marker.getTitle());
            //description.setText(marker.);

        }
        return (v);
    }

    @Override
    public View getInfoContents(Marker marker) {


        return (null);
    }
}
