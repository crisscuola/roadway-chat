package com.roadway.capslabs.roadway_chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
    Marker melbourne;
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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .bearing(90)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
        melbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne"));
        melbourne.showInfoWindow();

        for (int i = 0; i < 3; i++){
            Random rnd = new Random(System.currentTimeMillis());
            int lat = 50 + rnd.nextInt(100 - 50 + 1);
            int lng = 50 + rnd.nextInt(100 - 50 + 1);
            LatLng latLng = new LatLng(lat, lng);

            String title = "title" + " ( " +lat+","+lng+ " ) ";
            setMarker(latLng, mMap, title);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {

            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.equals(melbourne)) {
                    Intent intent = new Intent(context, SingleEventActivity.class);

                    intent.putExtra("id", 3);
                    startActivity(intent);
                    Log.d("marker", "CLICK!!");
                }
            }

        });

    }




    private void setMarker(LatLng latLng, GoogleMap googleMap, String title){
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
