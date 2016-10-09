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
        try {
            new DownloadingImage().execute(event.getUrl()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rowView;
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    private class DownloadingImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            url = getImageUrl(url);
            Bitmap image = null;
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.i("exception", "closing");
                }
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }
    }
}
