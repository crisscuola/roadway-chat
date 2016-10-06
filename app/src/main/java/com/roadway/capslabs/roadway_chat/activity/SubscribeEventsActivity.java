package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikepenz.materialdrawer.Drawer;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.adapters.EventsAdapter;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.models.DateRange;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;
import com.roadway.capslabs.roadway_chat.network.HttpConnectionHandler;
import com.vk.sdk.VKSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 06.10.16.
 */
public class SubscribeEventsActivity extends AppCompatActivity {


    private final static DrawerFactory drawerFactory;
    private final static HttpConnectionHandler handler;

    private ListView listView;
    private Drawer drawer;
    private Toolbar toolbar;

    private EventsAdapter eventsAdapter;
    private final Activity context = this;

    static {
        handler = new HttpConnectionHandler();
        drawerFactory = new DrawerFactory(handler);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initToolbar(getString(R.string.title_activity_sub));
        drawer = drawerFactory.getDrawerBuilder(this, toolbar).build();
        initAdapter();
        VKSdk.initialize(this);

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

    private void initAdapter() {
        listView = (ListView) findViewById(R.id.events_list);
        eventsAdapter = new EventsAdapter(this);
        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(SubscribeEventsActivity.this, SingleEventActivity.class);
                myIntent.putExtra("item", i);
                startActivity(myIntent);
            }
        });
    }

    private final class EventsLoader extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            EventRequestHandler handler = (EventRequestHandler) params[0];
            return handler.getSubsEvents(context);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_crete_event", result);
            JSONObject object = HttpConnectionHandler.parseJSON(result);
            try {
                JSONArray array = object.getJSONArray("object_list");
                List<Event> events = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject)array.get(i);
                    Event event = new Event("Title_mock", json.getString("about"),
                            "bytes_mock".getBytes(), new DateRange("18:00 01.10.2016", "18:00 01.10.2017"));
                    events.add(event);
                    eventsAdapter.add(event.getDescription());
                    eventsAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                throw new RuntimeException("JSON parsing error", e);
            }
        }
    }


}
