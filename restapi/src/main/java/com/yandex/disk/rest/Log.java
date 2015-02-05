package com.yandex.disk.rest;

public class Log {
    public static void d(String tag, String s, Throwable ex) {
        System.out.println(tag + " " + s);
        ex.printStackTrace(System.out);
    }

    public static void d(String s) {
        System.out.println(s);
    }

    public static void d(String tag, String s) {
        System.out.println(tag+" "+s);
    }

    public static void i(String tag, String s) {
        System.out.println(tag+" "+s);
    }

    public static void w(String tag, Throwable ex) {
        System.out.println(tag);
        ex.printStackTrace(System.out);
    }
}
