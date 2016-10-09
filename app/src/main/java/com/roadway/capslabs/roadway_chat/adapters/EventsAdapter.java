package com.roadway.capslabs.roadway_chat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kirill on 02.10.16
 */
public class EventsAdapter extends BaseAdapter {
    private List<Event> eventList = new ArrayList<>();
    private TextView textView, rating;
    private ImageView image;
    private Context context;

    public EventsAdapter(Context context) {
        this.context = context;
    }

    public void deleteMessage(int position){
        eventList.remove(position);
    }

    public void add(Event msg) {
        eventList.add(msg);
    }

    public void addEvents(List<Event> list) {
        eventList.addAll(list);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Event getItem(int position) {
        return this.eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.event_list_item, parent, false);
        textView = (TextView) rowView.findViewById(R.id.event_short_description);
        rating = (TextView) rowView.findViewById(R.id.rating);
        image = (ImageView) rowView.findViewById(R.id.image);
        Event event = getItem(position);
        textView.setText(event.getDescription());
        rating.setText(String.valueOf(event.getRating()));

        Picasso.with(context).load(getImageUrl(event.getUrl()))
                .fit()
                .placeholder(R.drawable.event_placeholder)
                .into(image);

        return rowView;
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }
}
