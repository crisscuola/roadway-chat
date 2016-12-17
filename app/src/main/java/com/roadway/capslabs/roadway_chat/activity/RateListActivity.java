package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.roadway.capslabs.roadway_chat.url.UrlType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kirill on 01.12.16
 */
public class RateListActivity extends AppCompatActivity{
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private EventsAdapter eventsAdapter;

    private Toolbar toolbar;
    private android.widget.SearchView searchView;
    private double lat, lng;
    private String email;

    private final Activity context = this;
    private LocationManager locationManager;
    private LatLng latLngl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("email")) {
                email = getIntent().getExtras().getString("email");
            }
        }

        setContentView(R.layout.activity_rate_list);
        initToolbar(getString(R.string.rate_activity_title));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lat = location.getLatitude();
        lng = location.getLongitude();

        latLngl = new LatLng(lat, lng);

        new EventsLoader().execute(new EventRequestHandler());
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }

    @Override
    public void onBackPressed() {

    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_rate);
        toolbar.setTitle(title);
        searchView = (android.widget.SearchView) findViewById(R.id.search_bar);
        // searchView.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {
        ListView listView = (ListView) findViewById(R.id.events_list);
        eventsAdapter = new EventsAdapter(this);
        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RateListActivity.this, RankActivity.class);
                Event event = eventsAdapter.getItem(i);
                Bundle bundle = new Bundle();
                bundle.putString("subscription_id", String.valueOf(event.getId()));
                intent.putExtras(bundle);
//                intent.putExtra("id", event.getId());
//                intent.putExtra("distance", event.getDistance());
                startActivity(intent);
            }
        });
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
            return handler.getAllEvents(context,lat,lng, UrlType.CONFIRMED);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_rate_list", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONArray array = object.getJSONArray("object_list");
                List<Event> events = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    JSONObject eventJson = json.getJSONObject("event");
                    Event event = new Event(eventJson);
                    events.add(event);
                }
                eventsAdapter.addEvents(events);
                eventsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}
