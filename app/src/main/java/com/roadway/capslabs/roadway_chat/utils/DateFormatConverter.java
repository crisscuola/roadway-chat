package com.roadway.capslabs.roadway_chat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kirill on 09.10.16
 */
public class DateFormatConverter {
    //18:00 01.10.2016
    //"HH:mm dd.MM.yyyy"
    public static String convertToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String convertToString(Date date) {
        return convertToString(date, DatePatternsConst.DEFAULT);
    }

    public static Date convertToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Exception while parsing date", e);
        }
    }

    public static Date convertToDate(String date) {
        return convertToDate(date, DatePatternsConst.DEFAULT);
    }
}
