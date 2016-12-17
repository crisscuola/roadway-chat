package com.roadway.capslabs.roadway_chat.models;

import com.orm.SugarRecord;

/**
 * Created by kirill on 19.10.16
 */
public class Code extends SugarRecord {
    int eventId;
    String code;

    public Code() {
    }

    public Code(int eventId, String code) {
        this.eventId = eventId;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isCached() {
        return !code.equals("0");
    }
}
