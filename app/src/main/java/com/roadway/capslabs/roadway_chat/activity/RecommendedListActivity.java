package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.FeedRecyclerViewAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by konstantin on 14.01.17.
 */
public class RecommendedListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;

    private RecyclerView recyclerView;
    private FeedRecyclerViewAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Drawer drawer;
    private Toolbar toolbar;
    private double lat, lng;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final Activity context = this;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory();
    }

    private LocationManager mLocationManager;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommended);
        initToolbar(getString(R.string.title_activity_sub));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            return;
        }

        initAdapter();

//        location = getLocation();
//        lat = location.latitude;
//        lng = location.longitude;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String email_s = "Lolo";
        email_s = sharedPref.getString("email", "Guest");

        Log.d("SHARED", email_s);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(new int[]{R.color.black});

        Log.d("Location", String.valueOf(lat) + " " + String.valueOf(lng));

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60 * 1000, 10, locationListener);

        Location location = getLastKnownLocation();
        lat = location.getLatitude();
        lng = location.getLongitude();

        new EventsLoader().execute(new EventRequestHandler());

    }

    @Override
    protected void onStop() {
        super.onStop();
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
        recyclerView = (RecyclerView) findViewById(R.id.favor_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new FeedRecyclerViewAdapter(this, new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent myIntent = new Intent(RecommendedListActivity.this, SingleEventActivity.class);
                myIntent.putExtra("id", event.getId());
                startActivity(myIntent);
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
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

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
//            Log.d(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
//            Log.d(TAG, latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            // editLocation.setText(s);
            Log.d("check", s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private final class EventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            return handler.getFavoritesEvents(context, lat,lng);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_farovtite", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);

            try {
                JSONArray array = object.getJSONArray("object_list");

                if (array.length() == 0) {
                    TextView noItemsTextView = (TextView) findViewById(R.id.no_favor_textview);
                    noItemsTextView.setVisibility(View.VISIBLE);

                    return;
                }

                List<Event> events = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject)array.get(i);
                    Event event = new Event(json);
                    events.add(event);
                }
                recyclerAdapter.addEvents(events);
                recyclerAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}