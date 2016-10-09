package com.roadway.capslabs.roadway_chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roadway.capslabs.roadway_chat.activity.SingleEventActivity;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        for (int i = 0; i < 5; i++) {

            Random rnd = new Random(System.currentTimeMillis());
            float lat = 1 + rnd.nextInt(50 - 1 + 1);
            float lng = 1 + rnd.nextInt(50 - 1 + 1);

            lat = lat/100 + 55.5f;
            lng = lng/100 + 37.5f;

            LatLng latlng = new LatLng(lat, lng);
            setMarker(latlng, mMap, String.valueOf(i));
        }

        int id = 0;

        if (getIntent().hasExtra("selected_event")) {
            id  = (int) getIntent().getExtras().get("selected_event");
            Log.d("intent", String.valueOf(id));

            LatLng latlng = new LatLng(55.745609, 37.614619);

            setMarker(latlng , mMap, String.valueOf(id));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(context, SingleEventActivity.class);
                    if (getIntent().hasExtra("selected_event")) {
                        intent.putExtra("id", Integer.parseInt(marker.getTitle()));
                        startActivity(intent);
                    } else {
                        intent.putExtra("id", Integer.parseInt(marker.getTitle()));
                        startActivity(intent);
                    }

                    Log.d("marker", "CLICK!!");

            }
        });

    }

    public void setMarker(LatLng latLng, GoogleMap googleMap, String title){
        Marker marker;
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        marker.showInfoWindow();
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
}
