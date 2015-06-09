/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ISO8601Test {

    @Test
    public void testFromTo() throws Exception {
        Calendar calendar = GregorianCalendar.getInstance();
        assertTrue(calendar.getTimeInMillis() / 1000L
                == ISO8601.toCalendar(ISO8601.fromCalendar(calendar)).getTimeInMillis() / 1000L);
    }

    @Test
    public void testParse() throws Exception {
        assertNull(ISO8601.parse(""));
        assertFalse(ISO8601.parse("2014-07-07T10:03:04+00:00").equals(new Date(1404727384001L)));
        assertTrue(ISO8601.parse("2014-12-09T20:45:57+04:00").equals(new Date(1418143557000L)));
    }
}
