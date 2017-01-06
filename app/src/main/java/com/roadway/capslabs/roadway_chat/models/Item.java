package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by konstantin on 17.12.16.
 */

public class Item {

   // private int mDrawableRes;
    private int mId;
    private String mTitle;
    private Event mEvent;

    public Item(String title, int id, Event event) {
      //  mDrawableRes = drawable;
        mTitle = title;
        mId =id;
        mEvent = event;
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

}