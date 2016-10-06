package com.roadway.capslabs.roadway_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 02.10.16
 */
public class EventsAdapter extends BaseAdapter {
    private List<Event> eventList = new ArrayList<>();
    private TextView textView, rating;
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
        Event event = getItem(position);
        textView.setText(event.getDescription());
        rating.setText(String.valueOf(event.getRating()));

        return rowView;
    }
}
