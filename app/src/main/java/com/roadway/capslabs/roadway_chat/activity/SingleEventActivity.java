package com.roadway.capslabs.roadway_chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.auth.ActivityAuth;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Code;
import com.roadway.capslabs.roadway_chat.models.CustomMarker;
import com.roadway.capslabs.roadway_chat.models.SingleEvent;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.share.ShareFb;
import com.roadway.capslabs.roadway_chat.share.ShareVk;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.roadway.capslabs.roadway_chat.url.UrlType;
import com.roadway.capslabs.roadway_chat.utils.ConnectionChecker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by konstantin on 02.10.16
 */
public class SingleEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Activity context = this;
    private OnMapReadyCallback callback = this;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory();
    private Drawer drawer;

    private ImageView imageView, imageQr, arrow, star;
    private TextView title, description, rating, address, metro, dateEnd, creator, adres;
    private RelativeLayout phone, url, share;
    private Button showQr, vk, fb, add;
    private SingleEvent event;
    private ProgressBar progressBar;
    private boolean favor;
    private String fullDescription;

    private GoogleMap mMap;
    private Map<Marker, CustomMarker> markersMap = new HashMap<Marker, CustomMarker>();
    private Map<Integer, String> colors = new HashMap<Integer, String>();
    private int id;
    private String codeJson = "https://ru.wikipedia.org/wiki/QR";
    private Button again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(SingleEventActivity.this,
                new String[]{android.Manifest.permission.CALL_PHONE}, 1);

        if (!ConnectionChecker.isOnline(this)) {
            ConnectionChecker.showNoInternetMessage(this);
            setContentView(R.layout.no_internet);
            initTool(getString(R.string.single_event_title));
            drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

            again = (Button) findViewById(R.id.button_again);

            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleEventActivity.class);
                    finish();
                    startActivity(intent);
                }
            });

            return;
        }

        setContentView(R.layout.activity_single_event);
        initViews();
        initToolbar(getString(R.string.single_event_title));

        id = getIntent().getExtras().getInt("id");

        new EventLoader().execute(id);

        showQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ConnectionChecker.isOnline(context)) {
                    ConnectionChecker.showNoInternetMessage(context);
                    return;
                }

                if (isLoggedIn()) {
                    Code code = hasSeenQr();
                    if (code.isCached()) {
                        Bitmap bitmap = qrGenenartor(code.getCode());
                        showQrCodeActivity(bitmap);
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    new Subscriber().execute(id);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pls_log, Toast.LENGTH_SHORT)
                            .show();
                    Intent intent = new Intent(context, ActivityAuth.class);
                    startActivity(intent);
                }
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleEventActivity.this, MapsActivity.class);
                intent.putExtra("selected_event", id);
                intent.putExtra("title", event.getTitle());
                intent.putExtra("latitude", event.getLet());
                intent.putExtra("longitude", event.getLng());
                intent.putExtra("about", event.getDescription());
                //intent.putExtra("distance", distance);
                startActivity(intent);
            }
        });

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("add", "STAR CLICK !!!");

                if (isLoggedIn()) {


                    if (favor) {
                        new UnFavoriter().execute(id);
                        star.setImageResource(R.drawable.favorite_off);
                        star.refreshDrawableState();
                    } else {
                        new Favoriter().execute(id);
                        star.setImageResource(R.drawable.favorite_on2);
                        star.refreshDrawableState();
                    }
                    favor = !favor;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pls_log, Toast.LENGTH_SHORT)
                            .show();
                    Intent intent = new Intent(context, ActivityAuth.class);
                    startActivity(intent);
                }
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(event.getUrl()));
                startActivity(intent);
                startActivity(intent);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri = "tel:" + event.getPhone().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("add", "Click share!!");
//                getAlert();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String url = "http://178.62.255.248/event/view/" + id + "/unauthorized";
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);

                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer();
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

    public void initTool(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_no);
        toolbar.setTitle(title);
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        drawer = drawerFactory.getDrawerBuilderWithout(this)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        onBackPressed();
                        return false;
                    }
                })
                .build();

        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        address = (TextView) findViewById(R.id.address);
        metro = (TextView) findViewById(R.id.metro);
        adres = (TextView) findViewById(R.id.addres);
        rating = (TextView) findViewById(R.id.rating);
        creator = (TextView) findViewById(R.id.creator);
        dateEnd = (TextView) findViewById(R.id.date);
        showQr = (Button) findViewById(R.id.btn_show_qr);

//        url = (TextView) findViewById(R.id.url);
//        phone = (TextView) findViewById(R.id.phone);
//        share = (TextView) findViewById(R.id.share);
        url = (RelativeLayout) findViewById(R.id.relative_url);
        phone = (RelativeLayout) findViewById(R.id.relative_call);
        share = (RelativeLayout) findViewById(R.id.relative_share);
        //distanceView = (TextView) findViewById(R.id.distance_view);
//        code = (TextView) findViewById(R.id.code);
//        code.setVisibility(View.INVISIBLE);
//        vk = (Button) findViewById(R.id.vk);
//        fb = (Button) findViewById(R.id.fb);
//        url = (TextView) findViewById(R.id.url);
//        phone = (TextView) findViewById(R.id.phone);

        add = (Button) findViewById(R.id.add);
        address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        imageQr = (ImageView) findViewById(R.id.qr_image);
        arrow = (ImageView) findViewById(R.id.arrow);
        star = (ImageView) findViewById(R.id.star);
        star.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
    }

    private boolean isSubscribed(JSONObject event) {
        try {
            return event.getBoolean("subscribed");
        } catch (JSONException e) {
            throw new RuntimeException("Key \'subscribed\' not found", e);
        }
    }

    private boolean isFavor(JSONObject event) {
        try {
            return event.getBoolean("favourite");
        } catch (JSONException e) {
            throw new RuntimeException("Key \'favourite\' not found", e);
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
        if (isSubscribed(event) && isUsed(event)) {
            Code code = hasSeenQr();
            if (code.isCached()) {
                code.delete();
            }
        }
    }

    private void displayEventContent(JSONObject eventObj) {
        event = new SingleEvent(eventObj);
        String description = event.getDescription();
        fullDescription = description;
        if (description.length() > 250) {
            description = description.substring(0, 250);
            description += "...";
            arrow.setVisibility(View.VISIBLE);

            this.description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SingleEventActivity.this, EventDescriptionActivity.class);
                    intent.putExtra("description", fullDescription);
                    startActivity(intent);
                }
            });
        }

        this.title.setText(event.getTitle());
        this.description.setText(description);
        rating.setText(String.valueOf(event.getCountUsed()));
        //address.setText(String.valueOf(event.getAddress()));
        //address.setText("Route to Discount");

        //adres.setText(adressParse(String.valueOf(event.getAddress())));
        adres.setText(event.getAddress());
        dateEnd.setText(event.getDateEnd());
        //url.setText(event.getUrl());
        //phone.setText(event.getPhone());
        //String distanceToEvent = "Distance to this event: " + distance + " km";
        //distanceView.setText(distanceToEvent);
        String metroStation = "м. " + (event.getMetro());
//
//        try {
//            new ProfileLoader().execute(Integer.valueOf(eventObj.getString("user_id")));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        creator.setText(event.getOrganization());

        this.metro.setText(metroStation);
        metro.setTextColor(Color.parseColor(metroColor(event.getColor())));
        Picasso.with(context).load(getImageUrl(event.getPictureUrl()))
                .placeholder(R.drawable.event_placehold_m)
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


    private String metroColor(int colorNum) {
        colors.put(0, "#000000");
        colors.put(1, "#ef1e25");
        colors.put(2, "#029a55");
        colors.put(3, "#0252a2");
        colors.put(4, "#019ee0");
        colors.put(5, "#745c2f");
        colors.put(6, "#fbaa33");
        colors.put(7, "#b61d8e");
        colors.put(8, "#ffd803");
        colors.put(9, "#acadaf");
        colors.put(10, "#b1d332");
        colors.put(11, "#5091bb");
        colors.put(12, "#ffa8af");

        String color = "#000";

        color = colors.get(colorNum);

        return color;
    }

    private String adressParse(String adress) {
        String[] array = adress.split(",");
        for (int i = 0; i < array.length; i++) {
            Log.d("add", array[i]);
            // array[i] = array[i].replaceAll(" ", "");
        }

        adress = "г." + array[2] + ", ул." + array[0] + ", д. " + array[1];

        Log.d("add", adress);

        return adress;
    }

    protected void getAlert() {
        final CharSequence[] items = {"Vk", "Fb"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Choose SocialNetwork");
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Log.d("add", "vk");
                    Intent intent = new Intent(context, ShareVk.class);
                    String url = "http://p30700.lab1.stud.tech-mail.ru/event/view/" + id + "/unauthorized";
                    intent.putExtra("url", url);
                    intent.putExtra("title", event.getTitle());
                    startActivity(intent);

                }
                if (which == 1) {
                    Log.d("add", "fb");
                    Intent intent = new Intent(context, ShareFb.class);
                    String url = "http://p30700.lab1.stud.tech-mail.ru/event/view/" + id + "/unauthorized";
                    intent.putExtra("url", url);
                    intent.putExtra("title", event.getTitle());
                    startActivity(intent);

                    Log.d("share", "Share Vk");
                }
            }

        });
        dialogBuilder.create().show();

        return;
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        int id = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

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


    public Bitmap resizeMarker() {
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    public void setMarker(LatLng latLng, GoogleMap googleMap, String title, CustomMarker customMarker) {
        Marker marker;
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(latLng));//.icon(BitmapDescriptorFactory.fromBitmap(resizeMarker())));
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
            if (result.equals("Timeout")) {
                Log.d("Time", "Timeout EventLoader");
                setContentView(R.layout.no_internet);
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", id);
                        finish();
                        startActivity(intent);
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                JSONObject object = HttpConnectionHandler.parseJSON(result);
                try {
                    JSONObject eventObj = object.getJSONObject("object");
                    removeCodeIfUsed(eventObj);
                    displayEventContent(eventObj);
                    if (isFavor(eventObj)) {
                        Log.d("FAVOR", "T");
                        favor = true;
                        star.setImageResource(R.drawable.favorite_on2);
                    } else {
                        Log.d("FAVOR", "F");
                        favor = false;
                        star.setImageResource(R.drawable.favorite_off);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException("Error while parsing json", e);
                }

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                if (mapFragment == null) {
                    Log.d("call", "null");
                } else

                mapFragment.getMapAsync(callback);

            }
        }
    }

    private boolean isLoggedIn() {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        HttpUrl url = UrlType.FEED.getUrl().build();
        List<Cookie> cookies = cookieJar.loadForRequest(url);
        for (Cookie cookie : cookies) {
            if ("sessionid".equals(cookie.name())) {
                Log.d("response_auth_session", cookie.value());
                return true;
            }
        }
        return false;
    }

    private void saveCode(JSONObject event) {
        Code result;
        try {
            int eventId = event.getInt("id");
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
        protected void onPreExecute() {
            super.onPreExecute();
            showQr.setEnabled(false);
            showQr.setTextColor(getResources().getColor(R.color.l_9));
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().subscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //removeCodeIfUsed(true);

            progressBar.setVisibility(View.GONE);
            showQr.setEnabled(true);
            showQr.setTextColor(getResources().getColor(R.color.black));

            if (s.equals("Timeout")) {
                Log.d("Time", "Timeout Subscriber");
                setContentView(R.layout.no_internet);
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", id);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {
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
    }

    private final class Favoriter extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            star.setEnabled(false);
            //showQr.setTextColor(getResources().getColor(R.color.l_9));
        }

        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().favoriteEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            star.setEnabled(true);
            if (s.equals("Timeout")) {
                Log.d("Time", "Timeout Favoriter");
                setContentView(R.layout.no_internet);
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", id);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {
                //removeCodeIfUsed(true);
                Log.d("response_favorite", s);
                JSONObject object = HttpConnectionHandler.parseJSON(s);
            }
        }
    }

    private final class UnFavoriter extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            star.setEnabled(false);
        }

        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf(params[0]);
            return new EventRequestHandler().unfavotiteEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            star.setEnabled(true);
            if (s.equals("Timeout")) {
                Log.d("Time", "Timeout UnFavoriter");
                setContentView(R.layout.no_internet);
                again = (Button) findViewById(R.id.button_again);

                again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", id);
                        finish();
                        startActivity(intent);
                    }
                });
            }
            else {
                //removeCodeIfUsed(true);
                Log.d("response_favorite", s);
                JSONObject object = HttpConnectionHandler.parseJSON(s);
            }
        }
    }
}
