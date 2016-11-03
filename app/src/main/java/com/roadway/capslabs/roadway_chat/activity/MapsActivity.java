package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Toolbar toolbar;

    Activity context = this;
    List<Event> events = new ArrayList<>();
    private Map<Marker, Integer> markersMap = new HashMap<Marker, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initToolbar(getString(R.string.title_activity_maps));
        drawerFactory.getDrawerBuilder(this, toolbar).build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_maps);
        toolbar.setTitle(title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        new EventsLoader().execute(new EventRequestHandler());

        mMap = googleMap;
        int id = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (getIntent().hasExtra("selected_event")) {
            id = (int) getIntent().getExtras().get("selected_event");
            double lat = (double) getIntent().getExtras().get("latitude");
            double lng = (double) getIntent().getExtras().get("longitude");
            Log.d("latlng", lat + " " + lng);
            Log.d("intent", String.valueOf(id));

            String title = (String) getIntent().getExtras().get("title");

            LatLng latlng = new LatLng(lat, lng);

            setMarker(latlng, mMap, "id = " + String.valueOf(id) + " " + title, id);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        final int finalId = id;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(context, SingleEventActivity.class);
                if (getIntent().hasExtra("selected_event")) {
                    intent.putExtra("id", finalId);
                    startActivity(intent);
                } else {
                    intent.putExtra("id", markersMap.get(marker));
                    startActivity(intent);
                }

                Log.d("marker", "CLICK!!");
            }
        });
    }

    public void setMarker(LatLng latLng, GoogleMap googleMap, String title, int id) {
        Marker marker;
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        markersMap.put(marker, id);
        marker.showInfoWindow();
    }

    private void showEvents() {
        Log.d("events", String.valueOf(events.size()) + " " + events.get(0).getLet());
        for (Event event : events) {
            final LatLng latLng = new LatLng(event.getLet(), event.getLng());
            setMarker(latLng, mMap, event.getTitle(), event.getId());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final class EventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            final String allEvents = handler.getAllEvents(context);
            Log.d("response_create_alleve", allEvents);
            return allEvents;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_marker_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONArray array = object.getJSONArray("object_list");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    Event event = new Event(json);
                    events.add(event);
                }
                showEvents();
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}
