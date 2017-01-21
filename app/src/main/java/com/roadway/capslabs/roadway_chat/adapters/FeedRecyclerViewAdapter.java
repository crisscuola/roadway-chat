package com.roadway.capslabs.roadway_chat.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.models.Event;
import com.roadway.capslabs.roadway_chat.url.UrlConst;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 08.01.17
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {
    private List<Event> eventList = new ArrayList<>();

    private Activity context;
    private OnItemClickListener listener;

    public FeedRecyclerViewAdapter(Activity context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public FeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_cardview_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FeedRecyclerViewAdapter.ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        String title = event.getTitle();
        boolean favor = event.getFavor();

        if (favor) {
            Log.d("FAVOR", "T");
            holder.star.setVisibility(View.VISIBLE);
            holder.star.setImageResource(R.drawable.favorite_on2);
        } else {
            Log.d("FAVOR", "F");
            holder.star.setImageResource(R.drawable.favorite_off);
            holder.star.setVisibility(View.GONE);
        }

        holder.textView.setText(title);
        holder.rating.setText(String.valueOf(event.getRating()));
        String km = event.getDistance() + "km";
        holder.distance.setText(km);
        Log.d("recycler", holder.distance.getText().toString());


        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("add", "STAR CLICK !!!");
                Event event = eventList.get(holder.getAdapterPosition());
                int id = event.getId();
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(event);
            }
        });

        Picasso.with(context).load(getImageUrl(event.getPictureUrl()))
                .fit()
                .placeholder(R.drawable.event_placehold_m)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvents(List<Event> list) {
        eventList.addAll(list);
    }

    private String getImageUrl(String url) {
        return "http://" + UrlConst.URL + url;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, rating, distance;
        ImageView image, star;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.event_short_description);
            rating = (TextView) itemView.findViewById(R.id.rating);
            image = (ImageView) itemView.findViewById(R.id.image);
            distance = (TextView) itemView.findViewById(R.id.distance);
            star = (ImageView) itemView.findViewById(R.id.star_e);
            layout = (LinearLayout) itemView.findViewById(R.id.rv_item_layout);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event item);
    }
}
