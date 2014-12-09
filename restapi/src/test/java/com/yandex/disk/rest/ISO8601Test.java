package com.yandex.disk.rest;

import com.yandex.disk.rest.conv.ISO8601;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ISO8601Test {

    @Test
    public void testNow() throws Exception {
        Calendar calendar = GregorianCalendar.getInstance();
        System.out.println("calendar: " + calendar);
        String now = ISO8601.now();
        System.out.println("now: " + now);
        Calendar calendarNow = ISO8601.toCalendar(now);
        assertTrue(calendarNow.before(calendar));
    }

    @Test
    public void testParse() throws Exception {
        long time = ISO8601.parse("");
        assertTrue(time == 0);
    }
}
