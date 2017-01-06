package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.ItemAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.models.Item;
import com.roadway.capslabs.roadway_chat.models.MapsMarker;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ItemAdapter.ItemListener, RoutingListener,ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {

    private GoogleMap mMap;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;
    private Toolbar toolbar;
    private Activity context = this;
    private LatLng latLngl;
    private double lat, lng;
    private double distance;
    private View bottomSheet;
    private BottomSheetBehavior behavior;


    private static final String TAG = MapsActivity.class.getSimpleName();

    private List<Event> events = new ArrayList<>();
    private List<MapsMarker> markers = new ArrayList<>();
    private Map<Marker, Integer> markersMap = new HashMap<Marker, Integer>();
    private Map<Integer, List<Event>> eventMap = new HashMap<Integer, List<Event>>();
    private LocationManager locationManager;
    private LocationManager mLocationManager;
    private ItemAdapter mAdapter;

    private ClusterManager<MyItem> mClusterManager;
    private MyItem clickedClusterItem;
    private TextView adress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60 * 1000, 10, locationListener);

        Location location = getLastKnownLocation();

        lat = location.getLatitude();
        lng = location.getLongitude();

        latLngl = new LatLng(lat, lng);


        mapFragment.getMapAsync(this);

        initToolbar(getString(R.string.title_activity_maps));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
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


//        LatLng start = latLngl;
//        LatLng waypoint = latLngl;
//        LatLng end = new LatLng(55.751841, 37.623012);
//
//        Routing routing = new Routing.Builder()
//                .travelMode(Routing.TravelMode.WALKING)
//                .withListener(this)
//                .waypoints(start, waypoint, end)
//                .build();
//        routing.execute();

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
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
    }

    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_maps);
        toolbar.setTitle(title);
        adress =  (TextView) findViewById(R.id.bottom_adress);
    }

    private void addItems(LatLng latlng, String title, String adress, int id) {

        double lat = latlng.latitude;
        double lng = latlng.longitude;

        MyItem offsetItem = new MyItem(lat, lng, title , adress, id);
        mClusterManager.addItem(offsetItem);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        new EventsLoader().execute(new EventRequestHandler());

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        clickedClusterItem = item;
                        return false;
                    }
                });

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

        //mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        int id = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (getIntent().hasExtra("selected_event")) {
            id = (int) getIntent().getExtras().get("selected_event");
            double lat = (double) getIntent().getExtras().get("latitude");
            double lng = (double) getIntent().getExtras().get("longitude");
            Log.d("latlng", lat + " " + lng);
            Log.d("intent", String.valueOf(id));

            String title = (String) getIntent().getExtras().get("title");


            LatLng latlng = new LatLng(lat, lng);

            //setMarker(latlng, mMap, title, id);

            setRoute(latlng);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

            double targetLat = ( lat + latLngl.latitude ) / 2;
            double targetLng = ( lng + latLngl.longitude ) / 2;

            LatLng target = new LatLng(targetLat,targetLng);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(target)
                    .zoom(10)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }  else {

        new EventsLoader().execute(new EventRequestHandler());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngl)
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

//        final int finalId = id;
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//
//                Intent intent = new Intent(context, SingleEventActivity.class);
//                if (getIntent().hasExtra("selected_event")) {
//                    intent.putExtra("id", finalId);
//                    //intent.putExtra("distance", distance);
//                    startActivity(intent);
//                } else {
//                    intent.putExtra("id", markersMap.get(marker));
//                    startActivity(intent);
//                }
//                Log.d("marker", "CLICK!!");
//            }
//        });
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem myItem) {

        if (myItem.getTitle().equals("some title")){
            //do something specific to this InfoWindow....
        }
    }

    public void setRoute(LatLng endPoint) {
        LatLng start = latLngl;
        LatLng waypoint = latLngl;
        LatLng end = endPoint;

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(start, waypoint, end)
                .build();
        routing.execute();
    }

    public void showSheet(int id) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ItemAdapter(createItems(id), this);
        recyclerView.setAdapter(mAdapter);
    }

    public void setMarker(LatLng latLng, GoogleMap googleMap, String title, int id) {
        Marker marker;
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        markersMap.put(marker, id);
    }

    private void showEvents() {
//        Log.d("events", String.valueOf(events.size()) + " " + events.get(0).getLet());
//        for (Event event : events) {
//            final LatLng latLng = new LatLng(event.getLet(), event.getLng());
//            addItems(latLng,event.getTitle());
////            setMarker(latLng, mMap, event.getTitle(), event.getId());
//        }

        Log.d("FUС", String.valueOf(markers.size()));

        for (MapsMarker marker : markers) {
            final LatLng latLng = new LatLng(marker.getLat(), marker.getLng());
            addItems(latLng,  marker.getName(), marker.getAdress(), marker.getId());
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // marker.getTitle();
        //mAdapter = new ItemAdapter(createItems(), this);
        recyclerView.setAdapter(mAdapter);
//        marker.showInfoWindow();
        return true;
    }

    public List<Item> createItems(int id) {
        ArrayList<Item> items = new ArrayList<>();

        List<Event> eve = eventMap.get(id);

        for (int i = 0; i < eve.size();  i++) {
             Log.d("SER", String.valueOf(eve.get(i).getTitle()) + i );
            items.add(new Item(eve.get(i).getTitle(),eve.get(i).getId(), eve.get(i)));
        }

        return items;
    }


    @Override
    public void onItemClick(Item item) {
        //behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        int id  = item.getmId();
        //item.getmEvent().getId();
        //Event event = events.get(id);
        Intent intent = new Intent(MapsActivity.this, SingleEventActivity.class);
        intent.putExtra("id", item.getmEvent().getId());
        intent.putExtra("distance", item.getmEvent().getDistance());
        startActivity(intent);
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

        List<LatLng> points = arrayList.get(0).getPoints();

        PolylineOptions options = new PolylineOptions();

        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );

        for ( LatLng locRecorded : points )
        {
            options.add( locRecorded );
        }

        mMap.addPolyline( options );
    }

    @Override
    public void onRoutingCancelled() {

    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.marker_window, null);
        }
        @Override
        public View getInfoWindow(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.marker_title));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.marker_description));

            tvTitle.setText(clickedClusterItem.getTitle());
            tvSnippet.setText(clickedClusterItem.getSnippet());

            String title = clickedClusterItem.getSnippet();

            int id = clickedClusterItem.getId();

            Log.d("FU", title);

            showSheet(id);
            adress.setText(title);

            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
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
            final String allEvents = handler.getMapEvents(context,lat ,lng);
            Log.d("response_create_alleve", allEvents);
            return allEvents;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_marker_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONArray array = object.getJSONArray("object_list");

                int identy;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    MapsMarker mapsMarker = new MapsMarker(json);
                  //  Event event = new Event(json);
                    identy = mapsMarker.getId();



                    Log.d("FU_", String.valueOf(identy));
                    markers.add(mapsMarker);

                    List<Event> eventes = new ArrayList<>();

                    try {
                        JSONArray eve = json.getJSONArray("events");

                        for (int j = 0; j < eve.length(); j++) {

                            JSONObject jsonE = (JSONObject) eve.get(j);
                            Event event = new Event(jsonE);

                            eventes.add(event);
                        }
                        eventMap.put(identy,eventes);


                    } catch (JSONException e) {
                        throw new RuntimeException("JSON parsing error", e);
                    }

                    //events.add(event);
                }

                showEvents();

            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }
}