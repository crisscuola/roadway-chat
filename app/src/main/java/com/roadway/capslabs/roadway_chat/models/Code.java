package com.roadway.capslabs.roadway_chat.models;

import com.orm.SugarRecord;

/**
 * Created by kirill on 19.10.16
 */
public class Code extends SugarRecord {
    int eventId;
    int code;

    public Code() {
    }

    public Code(int eventId, int code) {
        this.eventId = eventId;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
