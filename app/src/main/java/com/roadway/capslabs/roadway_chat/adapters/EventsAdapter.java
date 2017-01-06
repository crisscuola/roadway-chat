package com.roadway.capslabs.roadway_chat.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 02.10.16
 */
public class EventsAdapter extends BaseAdapter {
    private List<Event> eventList = new ArrayList<>();
    private TextView textView, rating, distance;
    private ImageView image,star;
    private Activity context;
    private boolean favor;


    public EventsAdapter(Activity context) {
        this.context = context;
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
        distance = (TextView) rowView.findViewById(R.id.distance);
        star = (ImageView)  rowView.findViewById(R.id.star_e);
        Event event = getItem(position);
        String description = event.getDescription();
        String title = event.getTitle();
        favor = event.getFavor();

        if (description.length() > 50) {
            description = description.substring(0, 50);
            description += "...";
        }

        if (favor) {
            Log.d("FAVOR", "T");
            star.setImageResource(R.drawable.favorite_on);
        } else  {
            Log.d("FAVOR","F");
            star.setImageResource(R.drawable.favorite_off);
        }

//        textView.setText(description);
        textView.setText(title);
        rating.setText(String.valueOf(event.getRating()));
        String km = event.getDistance() + "km";
        distance.setText(km);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("add", "STAR CLICK !!!");
                Event event = getItem(position);
                int id = event.getId();

//                if (favor)  {
//                    new UnFavoriter().execute(id);
//                    favor = !favor;
//                    star.setImageResource(R.drawable.favorite_off);
//                    star.refreshDrawableState();
//                } else {
//                    new Favoriter().execute(id);
//                    star.setImageResource(R.drawable.favorite_on);
//                    star.refreshDrawableState();
//                    }
//                favor = !favor;

                }

        });

        Picasso.with(context).load(getImageUrl(event.getPictureUrl()))
                .fit()
                .placeholder(R.drawable.event_placeholder)
                .into(image);

        return rowView;
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    private final class Favoriter extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().favoriteEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //removeCodeIfUsed(true);
            Log.d("response_favorite", s);
            JSONObject object = HttpConnectionHandler.parseJSON(s);
//            try {
//                JSONObject eventObj = object.getJSONObject("object");
//                codeJson = (String) eventObj.get("activate_link");
//                Bitmap bitmap = qrGenenartor(codeJson);
//                saveCode(eventObj);
//                showQrCodeActivity(bitmap);
//            } catch (JSONException e) {
//                throw new RuntimeException("JSON parsing error", e);
//            }
        }
    }

    private final class UnFavoriter extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().unfavotiteEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //removeCodeIfUsed(true);
            Log.d("response_favorite", s);
            JSONObject object = HttpConnectionHandler.parseJSON(s);
//            try {
//                JSONObject eventObj = object.getJSONObject("object");
//                codeJson = (String) eventObj.get("activate_link");
//                Bitmap bitmap = qrGenenartor(codeJson);
//                saveCode(eventObj);
//                showQrCodeActivity(bitmap);
//            } catch (JSONException e) {
//                throw new RuntimeException("JSON parsing error", e);
//            }
        }
    }
}
