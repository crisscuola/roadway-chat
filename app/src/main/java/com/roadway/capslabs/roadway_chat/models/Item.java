package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by konstantin on 17.12.16.
 */

public class Item {

   // private int mDrawableRes;
    private int mId;
    private String mTitle;
    private Event mEvent;
    private int mRating;

    public Item(String title, int id, Event event, int rating) {
      //  mDrawableRes = drawable;
        mTitle = title;
        mId =id;
        mEvent = event;
        mRating = rating;
    }



//    public int getDrawableResource() {
//        return mDrawableRes;
//    }

    public String getTitle() {
        return mTitle;
    }

    public int getmId() {
        return mId;
    }

    public  Event getmEvent() {return mEvent;}

    public int getmRating() {return mRating;
    }
}