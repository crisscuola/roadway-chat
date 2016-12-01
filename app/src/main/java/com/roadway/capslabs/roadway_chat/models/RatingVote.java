package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by kirill on 28.11.16
 */
public class RatingVote {
    private final int stars;
    private final String text;

    public RatingVote(int stars, String text) {
        this.stars = stars;
        this.text = text;
    }

    public float getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }
}
