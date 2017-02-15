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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.FeedRecyclerViewAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.drawer.DrawerUtils;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlType;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kirill on 01.12.16
 */
public class RateListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;

    private FeedRecyclerViewAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private android.widget.SearchView searchView;
    private double lat, lng;
    private String email;
    private ProgressBar progressBar;

    private final Activity context = this;
    private LocationManager locationManager;
    private LatLng latLngl;
    private LocationManager mLocationManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Map<Integer, Integer> subs = new HashMap<Integer, Integer>();
    private Button again;

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        drawer = drawerFactory.getDrawerBuilder(this, toolbar)
                .withSelectedItem(DrawerUtils.RANK_ITEM)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            setContentView(R.layout.no_internet);
            drawer = drawerFactory.getDrawerBuilder(this, toolbar)
                    .withSelectedItem(DrawerUtils.RANK_ITEM)
                    .build();

            again = (Button) findViewById(R.id.button_again);

            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RateListActivity.class);
                    finish();
                    startActivity(intent);
                }
            });

            return;
        }

        setContentView(R.layout.activity_rate_list);
        initToolbar(getString(R.string.rate_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar)
                .withSelectedItem(DrawerUtils.RANK_ITEM)
                .build();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("email")) {
                email = getIntent().getExtras().getString("email");
            }
        }

        initAdapter();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(new int[]{R.color.black});

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 0, 0, locationListener);

//        Location location = getLastKnownLocation();
//
//        lat = location.getLatitude();
//        lng = location.getLongitude();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        lat = getDouble(prefs, "lat", 55.797010);
        lng = getDouble(prefs, "lng", 37.537910);

        latLngl = new LatLng(lat, lng);

        new EventsLoader().execute(new EventRequestHandler());
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_rate);
        toolbar.setTitle(title);
        searchView = (android.widget.SearchView) findViewById(R.id.search_bar);
        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        // searchView.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {
        recyclerView = (RecyclerView) findViewById(R.id.rate_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new FeedRecyclerViewAdapter(this, new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent = new Intent(RateListActivity.this, RankActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subscription_id", String.valueOf(subs.get(event.getId())));
//                subs.get(String.valueOf(event.getId()));
                Log.d("Rank",String.valueOf(subs.get(event.getId())));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
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

    @Override
    public void onRefresh() {
        if (!ConnectionChecker.isOnline(context)) {
            ConnectionChecker.showNoInternetMessage(context);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);
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
            Log.d("OOO",handler.getAllEvents(context, lat, lng, UrlType.CONFIRMED));
            return handler.getAllEvents(context, lat, lng, UrlType.CONFIRMED);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_rate_list", result);

            if (result.equals("Timeout")) {
                Log.d("Time","Timeout RateList");
                setContentView(R.layout.no_internet);
                drawer = drawerFactory.getDrawerBuilder(context, toolbar)
                        .withSelectedItem(DrawerUtils.RANK_ITEM)
                        .build();

                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RateListActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {

                JSONObject object = HttpConnectionHandler.parseJSON(result);
                try {
                    JSONArray array = object.getJSONArray("object_list");

                    if (array.length() == 0) {
                        TextView noItemsTextView = (TextView) findViewById(R.id.no_rate_textview);
                        noItemsTextView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        return;
                    }

                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = (JSONObject) array.get(i);
                        JSONObject eventJson = json.getJSONObject("event");
                        Event event = new Event(eventJson);
                        subs.put(event.getId(), ((JSONObject) array.get(i)).getInt("id"));
                        Log.d("Rank", String.valueOf(((JSONObject) array.get(i)).getInt("id")));
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
