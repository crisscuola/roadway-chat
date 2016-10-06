package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.roadway.capslabs.roadway_chat.url.UrlConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by konstantin on 02.10.16
 */
public class SingleEventActivity extends AppCompatActivity {
    private Activity context = this;
    private Drawer drawer;
    private Toolbar toolbar;
    private final DrawerFactory drawerFactory = new DrawerFactory(new HttpConnectionHandler());

    private ImageView imageView;
    private TextView title;
    private TextView description;
    private TextView rating;
    private Button subscribe, unsubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        final int  id = getIntent().getExtras().getInt("id");
        initToolbar("SingleEventActivity");
        initViews();
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            new Subscriber().execute(id);
            }
        });

        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UnSubscriber().execute(id);
            }
        });



        new EventLoader().execute(id);
    }

    public void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle(title);
    }

    public void initViews() {
        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        rating = (TextView) findViewById(R.id.rating);
        subscribe = (Button) findViewById(R.id.btn_subs);
        unsubscribe = (Button) findViewById(R.id.btn_unsubs);
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
                Event event = new Event(eventObj);
                title.setText("Mock title");
                description.setText(event.getDescription());
                rating.setText(String.valueOf(event.getRating()));
                new DownloadingImage().execute(eventObj.getString("avatar"));
            } catch (JSONException e) {
                throw new RuntimeException("Error while parsing json", e);
            }
        }
    }

    private class DownloadingImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            url = getImageUrl(url);
            Bitmap image = null;
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.i("exception", "closing");
                }
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    private final class Subscriber extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf( params[0]);
            return new EventRequestHandler().subscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("response_subscribe", s);
        }
    }

    private final class UnSubscriber extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String id = String.valueOf( params[0]);
            return new EventRequestHandler().unsubscribeEvent(context, id);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("response_subscribe", s);
        }
    }
}
