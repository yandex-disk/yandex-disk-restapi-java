/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class OkHttpClientFactory {

    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 30 * 1000;
    private static final int WRITE_TIMEOUT_MILLIS = 30 * 1000;

    public static OkHttpClient makeClient() {
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        client.setFollowSslRedirects(true);
        client.setFollowRedirects(true);

        return client;
    }
}
