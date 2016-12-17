package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by konstantin on 17.12.16.
 */

public class Item {

    private int mDrawableRes;
    private int mId;
    private String mTitle;

    public Item(int drawable, String title, int id) {
        mDrawableRes = drawable;
        mId =id;
        mTitle = title;
    }

    public int getDrawableResource() {
        return mDrawableRes;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getmId() {
        return mId;
    }

}