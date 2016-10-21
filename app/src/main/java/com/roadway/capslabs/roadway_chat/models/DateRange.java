package com.roadway.capslabs.roadway_chat.models;

import java.util.Date;

/**
 * Created by kirill on 05.10.16
 */
public class DateRange {
//    private final Date dateStart;
//    private final Date dateEnd;
//
//    public DateRange(Date dateStart, Date dateEnd) {
//        this.dateStart = new Date(dateStart.getTime());
//        this.dateEnd = new Date(dateEnd.getTime());
//    }
//
//    public Date getDateStart() {
//        return new Date(dateStart.getTime());
//    }
//
//    public Date getDateEnd() {
//        return new Date(dateEnd.getTime());
//    }

    private final String dateStart;
    private final String dateEnd;

    public DateRange(String dateStart, String dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }
}
