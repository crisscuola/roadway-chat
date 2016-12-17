package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.EventsAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 06.10.16.
 */
public class FavoriteEventsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;

    private ListView listView;
    private Drawer drawer;
    private Toolbar toolbar;
    private double lat, lng;
    private LatLng location;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventsAdapter eventsAdapter;
    private final Activity context = this;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.title_activity_sub));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();
        //VKSdk.initialize(this);
        location = getLocation();
        lat = location.latitude;
        lng = location.longitude;

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(new int[]{R.color.colorToolbar});

        new EventsLoader().execute(new EventRequestHandler());

    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }


    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_feed);
        toolbar.setTitle(title);
    }

    private LatLng getLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

        return userLocation;
    }

    private void initAdapter() {
        listView = (ListView) findViewById(R.id.events_list);
        eventsAdapter = new EventsAdapter(this);
        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(FavoriteEventsActivity.this, SingleEventActivity.class);
                Event event = eventsAdapter.getItem(i);
                myIntent.putExtra("id", event.getId());
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdapter();
                new EventsLoader().execute(new EventRequestHandler());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 300);
    }

    private final class EventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            return handler.getSubsEvents(context, lat,lng);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_crete_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONArray array = object.getJSONArray("object_list");
                List<Event> events = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject)array.get(i);
                    Event event = new Event(json);
//                    Event event = new Event("Title_mock", json.getString("about"),
//                            "bytes_mock".getBytes(), new DateRange("18:00 01.10.2016", "18:00 01.10.2017"), 0.0f);
                    events.add(event);
//                    eventsAdapter.add(event.getDescription());
//                    eventsAdapter.notifyDataSetChanged();

                }
                eventsAdapter.addEvents(events);
                eventsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }


}
