package com.roadway.capslabs.roadway_chat.adapters;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.MapsActivity;
import com.roadway.capslabs.roadway_chat.models.Item;

import java.util.List;

/**
 * Created by konstantin on 17.12.16.
 */

public class CustomClusterItemAdapter extends RecyclerView.Adapter<CustomClusterItemAdapter.ViewHolder> {

    private List<Item> mItems;
    private ItemListener mListener;

    public CustomClusterItemAdapter(List<Item> items, MapsActivity listener) {
        mItems = items;
        mListener = listener;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));

        if(holder.getPosition() == position)
            holder.itemView.setBackgroundColor(Color.WHITE);
        else
            holder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView, rating;
        public Item item;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.textView);
//            imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            rating = (TextView) itemView.findViewById(R.id.bottom_rating);

        }

        public void setData(Item item) {
            this.item = item;
            //imageView.setImageResource(item.getDrawableResource());
            String title = item.getTitle();
            if (title.length() > 35 )
            {
                title = title.substring(0,32);
                title = title + " ...";
            }
            textView.setText(title);
            //rating.setText(String.valueOf(item.getmRating()));
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    public interface ItemListener {
        void onItemClick(Item item);

    }
}