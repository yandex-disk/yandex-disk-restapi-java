/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ISO8601 {

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat(FORMAT).format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat(FORMAT).parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public static Date parse(final String iso8601string) {
        try {
            return toCalendar(iso8601string).getTime();
        } catch (ParseException e) {
            return null;
        }
    }
}
