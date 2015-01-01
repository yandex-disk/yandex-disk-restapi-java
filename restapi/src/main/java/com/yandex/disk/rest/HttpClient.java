
package com.yandex.disk.rest;

//import android.net.SSLCertificateSocketFactory;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

//import retrofit.client.OkClient;

public class HttpClient extends OkClient {

    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 30 * 1000;
    private static final int WRITE_TIMEOUT_MILLIS = 30 * 1000;

//    private final OkHttpClient client;

    private static OkHttpClient makeClient() {
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        Log.d("getConnectTimeout: " + client.getConnectTimeout());
        Log.d("getReadTimeout: " + client.getReadTimeout());
        Log.d("getWriteTimeout: " + client.getWriteTimeout());

        client.setFollowSslRedirects(true);
        client.setFollowRedirects(true);

        // TODO XXX pinning
        // https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/com/squareup/okhttp/recipes/CertificatePinning.java
//        client.setSslSocketFactory(SSLCertificateSocketFactory.getDefault(CONNECT_TIMEOUT_MILLIS, null));

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