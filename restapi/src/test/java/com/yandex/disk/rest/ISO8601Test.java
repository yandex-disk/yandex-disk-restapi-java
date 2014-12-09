package com.yandex.disk.rest;

import com.yandex.disk.rest.conv.ISO8601;

import org.junit.Ignore;
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

    @Ignore
    @Test
    public void testNow() throws Exception {
        // TODO
//        String str = ISO8601.nowInSeconds();
//        ISO8601.toCalendar(str);
    }

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
