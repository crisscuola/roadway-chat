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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private Toolbar toolbar;
   // private LatLng location;
    private LatLng currentLocation = new LatLng(55.751841, 37.623012);
    private LatLng latLngl;
    private double lat, lng;
    private double distance;
    private View bottomSheet;
    private BottomSheetBehavior behavior;
    private TextView textView1, textView2;

    private static final String TAG = MapsActivity.class.getSimpleName();

    Activity context = this;
    List<Event> events = new ArrayList<>();
    private Map<Marker, Integer> markersMap = new HashMap<Marker, Integer>();
    private Map<Integer, Double> distanceMap = new HashMap<Integer, Double>();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        location = getLocation();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

//        mapFragment = ((SupportMapFragment) context.getChildFragmentManager().findFragmentById(R.id.map)).getMap();

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


        mapFragment.getMapAsync(this);

        initToolbar(getString(R.string.title_activity_maps));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        textView1 = (TextView) findViewById(R.id.textView_1);
        textView2 = (TextView) findViewById(R.id.textView_2);
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                Log.d(TAG, "onSlide: " + slideOffset);
            }

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "onStateChanged: " + newState);
                // React to state change
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }



    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_maps);
        toolbar.setTitle(title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        new EventsLoader().execute(new EventRequestHandler());

        mMap = googleMap;
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
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

            setMarker(latlng, mMap, title, id);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }  else {

        new EventsLoader().execute(new EventRequestHandler());

//        location = getLocation();


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngl)
                .zoom(10)
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
                    //intent.putExtra("distance", distance);
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
       // googleMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater(), title, "LOL"));
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        markersMap.put(marker, id);
        //marker.showInfoWindow();
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
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        String title = marker.getTitle();
        textView1.setText(title);
        textView2.setText(title);
        return true;
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

//            Toast.makeText(
//                    getBaseContext(),
//                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
//                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
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
            final String allEvents = handler.getAllEvents(context,lat ,lng);
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