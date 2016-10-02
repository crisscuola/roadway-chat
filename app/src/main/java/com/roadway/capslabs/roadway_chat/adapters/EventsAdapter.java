package com.roadway.capslabs.roadway_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 02.10.16
 */
public class EventsAdapter extends BaseAdapter {
    private List<String> chatMessagesList = new ArrayList<>();

    {
        chatMessagesList.add("lol1");
        chatMessagesList.add("lol2");
        chatMessagesList.add("lol3");
    }
    private TextView textView;
    private Context context;

    public EventsAdapter(Context context) {
        this.context = context;
    }

    public void deleteMessage(int position){
        chatMessagesList.remove(position);
    }

    @Override
    public int getCount() {
        return chatMessagesList.size();
    }

    @Override
    public String getItem(int position) {
        return this.chatMessagesList.get(position);
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
        String  text = getItem(position);
        textView.setText(text);

        return rowView;
    }
}
