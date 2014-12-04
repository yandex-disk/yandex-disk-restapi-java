package com.yandex.disk.rest;

public class Log {
    public static void d(String tag, String s, Throwable ex) {
        System.err.println(tag + " " + s);
        ex.printStackTrace(System.err);
    }

    public static void d(String tag, String s) {
        System.err.println(tag+" "+s);
    }

    public static void i(String tag, String s) {
        System.err.println(tag+" "+s);
    }

    public static void w(String tag, Throwable ex) {
        System.err.println(tag);
        ex.printStackTrace(System.err);
    }
}
