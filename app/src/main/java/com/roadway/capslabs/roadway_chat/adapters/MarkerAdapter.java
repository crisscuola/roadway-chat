package com.roadway.capslabs.roadway_chat.adapters;

/**
 * Created by konstantin on 06.11.16.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.roadway.capslabs.roadway_chat.R;

public class MarkerAdapter implements InfoWindowAdapter {
    LayoutInflater inflater = null;
    private TextView textViewTitle, description;
    private String desc;
    private String title;

    public MarkerAdapter(LayoutInflater inflater,String title, String desc) {
        this.inflater = inflater;
        this.desc = desc;
        this.title = title;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        View v = inflater.inflate(R.layout.marker_window, null);
//        if (marker != null) {
//            textViewTitle = (TextView) v.findViewById(R.id.marker_title);
//            description = (TextView) v.findViewById(R.id.marker_despription);
//
//            textViewTitle.setText(marker.getTitle());
//            description.setText(desc);
//
//        }
//        return (v);

        return (null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        View v = inflater.inflate(R.layout.marker_window, null);

        if (marker != null) {
            if (desc.length() > 30) {
                desc = desc.substring(0, 30);
                desc += "  ...";
            }
            textViewTitle = (TextView) v.findViewById(R.id.marker_title);
            description = (TextView) v.findViewById(R.id.marker_description);

            textViewTitle.setText(title);
            description.setText(desc);

        }

        return (v);
    }
}
