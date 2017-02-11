package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.EndlessRecyclerViewScrollListener;
import com.roadway.capslabs.roadway_chat.adapters.FeedRecyclerViewAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.utils.Cache;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends LocationActivityTemplate implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks {
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private final int step = 20;
    private Drawer drawer;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private FeedRecyclerViewAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;

    private android.widget.SearchView searchView;
    private double lat, lng;
    private String email;

    private final Activity context = this;
    private LocationManager locationManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LocationManager mLocationManager;

    private EndlessRecyclerViewScrollListener scrollListener;
    private Button again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LocationChecker.isLocationEnabled(this.context);

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            setContentView(R.layout.no_internet);
            drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
            again = (Button) findViewById(R.id.button_again);

            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FeedActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
            return;
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("email")) {
                email = getIntent().getExtras().getString("email");

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email);
                editor.commit();
            }
        }

        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.feed_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        buildGoogleApiClient();
        createLocationRequest();

        initAdapter();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(new int[]{R.color.black});

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }

//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location location = getLastKnownLocation();

//        getLocation();
//        Location location = getmLastLocation();
//
////        Log.d("SHIT", String.valueOf(extras.getDouble("lat")));
//
//        if (location == null) {
//            lat = 55.797332;
//            lng = 37.537236;
//        } else {
//            lat = location.getLatitude();
//            lng = location.getLongitude();
//        }
//
//        Log.d("SHIT", String.valueOf(lat));
//
//        new EventsLoader().execute(new EventRequestHandler());
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void putLocation(double lat, double lng) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        putDouble(editor, "lat", lat);
        putDouble(editor, "lng", lng);
        editor.commit();
    }

    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }


    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }

    @Override
    public void onConnected(Bundle arg0) {
        super.onConnected(arg0);

        getLocation();
        Location location = getmLastLocation();

        if (location == null) {
            lat = 55.797010;
            lng = 37.537910;
            Log.d("GEO", "location null");
        } else {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

        Log.d("SHIT", String.valueOf(lat));

        putLocation(lat,lng);

        if (Cache.isFeedEmpty())
            new EventsLoader().execute(new EventRequestHandler());
        else {
            recyclerAdapter.addEvents(Cache.getFeed());
            recyclerAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_feed);
        toolbar.setTitle(title);
        searchView = (android.widget.SearchView) findViewById(R.id.search_bar);
        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        // searchView.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {
        recyclerView = (RecyclerView) findViewById(R.id.feed_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new FeedRecyclerViewAdapter(this, new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent = new Intent(FeedActivity.this, SingleEventActivity.class);
                intent.putExtra("favourite", event.getFavor());
                intent.putExtra("id", event.getId());
                intent.putExtra("distance", event.getDistance());
                startActivity(intent);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(totalItemsCount);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void loadNextData(int offset) {
        Log.d("feed_activity", "load new data, " + offset);
        new NextEventsLoader().execute(new EventRequestHandler(), offset, step);
    }

    @Override
    public void onRefresh() {

        if (!ConnectionChecker.isOnline(context)) {
            ConnectionChecker.showNoInternetMessage(context);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        buildGoogleApiClient();
        createLocationRequest();

        progressBar.setVisibility(View.VISIBLE);
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

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private final class EventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            return handler.getAllEvents(context, lat, lng);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Cache.clearFeed();
            Log.d("response_crete_event", result);
            if (result.equals("Timeout")) {
                Log.d("Time","Timeout EventsFeeDLoader");
                setContentView(R.layout.no_internet);
                drawer = drawerFactory.getDrawerBuilder(context, toolbar).build();
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, FeedActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {
                JSONObject object = HttpConnectionHandler.parseJSON(result);
                try {
                    JSONArray array = object.getJSONArray("object_list");
                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = (JSONObject) array.get(i);
                        Event event = new Event(json);
                        events.add(event);
                    }

                    recyclerAdapter.addEvents(events);
                    recyclerAdapter.notifyDataSetChanged();
                    Cache.saveFeed(events);
                } catch (JSONException e) {
                    throw new RuntimeException("JSON parsing error", e);
                }
            }
        }
    }

    private final class NextEventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            int offset = (int) params[1];
            int step = (int) params[2];
            return handler.getNextEvents(context, lat, lng, offset, step);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_crete_event", result);
            if (result.equals("Timeout")) {
                Log.d("Time","Timeout NextEventSFeeDLoader");
                setContentView(R.layout.no_internet);
                drawer = drawerFactory.getDrawerBuilder(context, toolbar).build();
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, FeedActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {
                JSONObject object = HttpConnectionHandler.parseJSON(result);
                try {
                    JSONArray array = object.getJSONArray("object_list");
                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = (JSONObject) array.get(i);
                        Event event = new Event(json);
                        events.add(event);
                    }

                    recyclerAdapter.addEvents(events);
                    recyclerAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    throw new RuntimeException("JSON parsing error", e);
                }
            }
        }
    }
}
