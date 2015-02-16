
package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.okhttp.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class HttpClient extends OkClient {

    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 30 * 1000;
    private static final int WRITE_TIMEOUT_MILLIS = 30 * 1000;

    private final OkHttpClient client;

    private static OkHttpClient makeClient() {
        OkHttpClient client = new OkHttpClient();

        client.networkInterceptors().add(new LoggingInterceptor());

        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        client.setFollowSslRedirects(true);
        client.setFollowRedirects(true);

        return client;
    }

    public HttpClient() {
        this(makeClient());
    }

    public HttpClient(final OkHttpClient client) {
        this.client = client;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
