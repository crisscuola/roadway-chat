package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Code;
import com.roadway.capslabs.roadway_chat.models.CustomMarker;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by konstantin on 02.10.16
 */
public class SingleEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Activity context = this;
    private OnMapReadyCallback callback = this;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();

    private ImageView imageView, imageQr, arrow;
    private TextView title, description, rating, address, metro, dateEnd, creator;
    private Button showQr;
    private Event event;
    private MapView mapView;
    private GoogleMap mMap;
    private Map<Marker, CustomMarker> markersMap = new HashMap<Marker, CustomMarker>();
    private int id;
    private String codeJson = "https://ru.wikipedia.org/wiki/QR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        id = getIntent().getExtras().getInt("id");
        initToolbar("Discount");
        initViews();

        drawerFactory.getDrawerBuilder(this, toolbar).build();
        new EventLoader().execute(id);

        showQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Code code = hasSeenQr();
                if (code.isCached()) {
                    Bitmap bitmap = qrGenenartor(code.getCode());
                    showQrCodeActivity(bitmap);
                    return;
                }
                new Subscriber().execute(id);
            }
        });


//        showOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SingleEventActivity.this, MapsActivity.class);
//                intent.putExtra("selected_event", id);
//                intent.putExtra("title", event.getDescription());
//                intent.putExtra("latitude", event.getLet());
//                intent.putExtra("longitude", event.getLng());
//                startActivity(intent);
//            }
//        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleEventActivity.this, MapsActivity.class);
                intent.putExtra("selected_event", id);
                intent.putExtra("title", event.getDescription());
                intent.putExtra("latitude", event.getLet());
                intent.putExtra("longitude", event.getLng());
                startActivity(intent);
            }
        });


//        showQr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SingleEventActivity.this, QrCodeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private Code hasSeenQr() {
        List<Code> codes = Code.find(Code.class, "event_id = ?", String.valueOf(id));
        Code code = null;
        if (codes.size() != 0)
            code = codes.get(0);

        return code != null ? code : new Code(0, "0");
    }

    private void showQrCodeActivity(Bitmap bitmap) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        startActivity(intent);
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle(title);
    }

    public void initViews() {
        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        address = (TextView) findViewById(R.id.address);
        metro = (TextView) findViewById(R.id.metro);
        rating = (TextView) findViewById(R.id.rating);
        creator = (TextView) findViewById(R.id.creator);
        dateEnd = (TextView) findViewById(R.id.date_end);
        showQr = (Button) findViewById(R.id.btn_show_qr);
//        code = (TextView) findViewById(R.id.code);
//        code.setVisibility(View.INVISIBLE);
        address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        imageQr = (ImageView) findViewById(R.id.qr_image);
        arrow = (ImageView) findViewById(R.id.arrow);
    }

    private boolean isSubscribed(JSONObject event) {
        try {
            return event.getBoolean("subscribed");
        } catch (JSONException e) {
            throw new RuntimeException("Key \'subscribed\' not found", e);
        }
    }

    private boolean isUsed(JSONObject event) {
        try {
            return event.getBoolean("is_used");
        } catch (JSONException e) {
            throw new RuntimeException("Key \'subscribed\' not found", e);
        }
    }

    private void removeCodeIfUsed(JSONObject event) {
        if(isSubscribed(event) && isUsed(event)) {
            Code code = hasSeenQr();
            if (code.isCached()) {
                code.delete();
            }
        }
    }

    private void displayEventContent(JSONObject eventObj) {
        event = new Event(eventObj);

        String description = event.getDescription();
        if (description.length() > 250) {
            description = description.substring(0, 250);
            description += "...";
            arrow.setVisibility(View.VISIBLE);

            this.description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SingleEventActivity.this, EventDescriptionActivity.class);
                    intent.putExtra("description", event.getDescription());
                    startActivity(intent);
                }
            });
        }

        this.title.setText(event.getTitle());
        this.description.setText(description);
        rating.setText(String.valueOf(event.getRating()));
        address.setText(String.valueOf(event.getAddress()));
        dateEnd.setText(event.getDateEnd());
        String metroStation = "м. " + (event.getMetro());

        try {
            new ProfileLoader().execute(Integer.valueOf(eventObj.getString("user_id")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.metro.setText(metroStation);
        Picasso.with(context).load(getImageUrl(event.getPictureUrl()))
                .placeholder(R.drawable.event_placeholder)
                .into(imageView);
        //displayCode(getCode(String.valueOf(event.getId())));
    }

    private Bitmap qrGenenartor(String link) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = multiFormatWriter.encode(link, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        int id = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);


        String title = (String) getIntent().getExtras().get("title");
        if (event != null) {
            LatLng latlng = new LatLng(event.getLet(), event.getLng());

            final CustomMarker customMarker = new CustomMarker(event.getTitle(), event.getDescription(), event.getId());

            setMarker(latlng, mMap, "id = " + String.valueOf(id) + " " + title, customMarker);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    public void setMarker(LatLng latLng, GoogleMap googleMap, String title, CustomMarker customMarker) {
        Marker marker;
        mMap = googleMap;
        String lolo = "TEST!!!";
       // mMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater(), lolo));
        marker = mMap.addMarker(new MarkerOptions().position(latLng));//.title(title));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.subscribe_icon)));
        markersMap.put(marker, customMarker);

        marker.showInfoWindow();
    }

    private final class EventLoader extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().getEvent(context, id);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_get_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONObject eventObj = object.getJSONObject("object");
                removeCodeIfUsed(eventObj);
                displayEventContent(eventObj);
            } catch (JSONException e) {
                throw new RuntimeException("Error while parsing json", e);
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(callback);
        }
    }

//    private String getCode(String id) {
//        List<Code> codes = Code.find(Code.class, "event_id = ?", String.valueOf(id));
//        if (codes.size() != 0) {
//            return String.valueOf(codes.get(0).getCode());
//        }
//
////        return event.getCode();
//        return codeJson;
//        //return 0;
//    }

//    private void displayCode(String code) {
//        if (code != "") {
////            this.code.setText(String.valueOf(""));
//        }
//    }

    private void saveCode(JSONObject event) {
        Code result;
        try {
            int eventId = event.getInt("event");
            List<Code> codes = Code.find(Code.class, "event_id = ?", String.valueOf(eventId));
            result = new Code(eventId, event.getString("activate_link"));
            if (codes.size() != 0) {
                long codeId = codes.get(0).getId();
                result.setId(codeId);
                Log.d("response_sub_id", String.valueOf(codeId));
            }
        } catch (JSONException e) {
            throw new RuntimeException("JSON parsing error", e);
        }
        result.save();
        List<Code> list = Code.listAll(Code.class);
        Log.d("response_sub_list", String.valueOf(list.size()));
    }

    private final class Subscriber extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().subscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //removeCodeIfUsed(true);
            Log.d("response_subscribe", s);
            JSONObject object = HttpConnectionHandler.parseJSON(s);
            try {
                JSONObject eventObj = object.getJSONObject("object");
                codeJson = (String) eventObj.get("activate_link");
                Bitmap bitmap = qrGenenartor(codeJson);
                saveCode(eventObj);
                showQrCodeActivity(bitmap);
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }

//    private final class UnSubscriber extends AsyncTask<Integer, Void, String> {
//        @Override
//        protected String doInBackground(Integer... params) {
//            String id = String.valueOf(params[0]);
//            return new EventRequestHandler().unsubscribeEvent(context, id);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            removeCodeIfUsed(false);
//            Log.d("response_subscribe", s);
//        }
//    }

    private final class ProfileLoader extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            Log.d("response_profile", id);
            return new EventRequestHandler().getCreator(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("response_profile", s);
            JSONObject object = HttpConnectionHandler.parseJSON(s);
            try {
                JSONObject obj = object.getJSONObject("object");
                final String firstName = obj.getString("first_name");
                final String lastName = obj.getString("last_name");
                final String name = firstName + " " + lastName;
                creator.setText(name);
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
            Log.d("response_subscribe", s);
        }
    }
}
