package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.MapsActivity;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by konstantin on 02.10.16
 */
public class SingleEventActivity extends AppCompatActivity {
    private Activity context = this;
    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    private ImageView imageView;
    private TextView title, description, rating, address, metro;
    private Button subscribe, unsubscribe, showOnMap, showQr;
    private Event event;
    private MapView mapView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        final int id = getIntent().getExtras().getInt("id");
        initToolbar("SingleEventActivity");
        initViews();

        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        new EventLoader().execute(id);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Subscriber().execute(id);
            }
        });

        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UnSubscriber().execute(id);
            }
        });



//        showOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SingleEventActivity.this, MapsActivity.class);
//                intent.putExtra("selected_event", id);
//                intent.putExtra("title", event.getDescription());
//                intent.putExtra("latitude", event.getLet());
//                intent.putExtra("longitude", event.getLng());
//                startActivity(intent);
//            }
//        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleEventActivity.this, MapsActivity.class);
                intent.putExtra("selected_event", id);
                intent.putExtra("title", event.getDescription());
                intent.putExtra("latitude", event.getLet());
                intent.putExtra("longitude", event.getLng());
                startActivity(intent);
            }
        });


//        showQr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SingleEventActivity.this, QrCodeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle(title);
    }

    public void initViews() {
        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        address = (TextView) findViewById(R.id.address);
        metro = (TextView) findViewById(R.id.metro);
        rating = (TextView) findViewById(R.id.rating);
        subscribe = (Button) findViewById(R.id.btn_subs);
        unsubscribe = (Button) findViewById(R.id.btn_unsubs);
        //showQr = (Button) findViewById(R.id.btn_show_qr);

        address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private boolean isSubscribed(JSONObject event) {
        try {
            return event.getBoolean("subscribed");
        } catch (JSONException e) {
            throw new RuntimeException("Key \'subscribed\' not found", e);
        }
    }

    private void showSubscribeButton(boolean isSubscribed) {
        if (!isSubscribed) {
            subscribe.setVisibility(View.VISIBLE);
            unsubscribe.setVisibility(View.GONE);
            return;
        }
        subscribe.setVisibility(View.GONE);
        unsubscribe.setVisibility(View.VISIBLE);
    }

    private void displayEventContent(JSONObject eventObj) {
        event = new Event(eventObj);
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        rating.setText(String.valueOf(event.getRating()));
        address.setText(String.valueOf(event.getAddress()));
        metro.setText(String.valueOf(event.getMetro()));
        Picasso.with(context).load(getImageUrl(event.getPictureUrl()))
                .placeholder(R.drawable.event_placeholder)
                .into(imageView);
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    private final class EventLoader extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().getEvent(context, id);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_get_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONObject eventObj = object.getJSONObject("object");
                showSubscribeButton(isSubscribed(eventObj));
                displayEventContent(eventObj);
            } catch (JSONException e) {
                throw new RuntimeException("Error while parsing json", e);
            }
        }
    }

    private final class Subscriber extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().subscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showSubscribeButton(true);
            Log.d("response_subscribe", s);
        }
    }

    private final class UnSubscriber extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().unsubscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showSubscribeButton(false);
            Log.d("response_subscribe", s);
        }
    }
}
