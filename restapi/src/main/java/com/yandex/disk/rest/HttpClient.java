
package com.yandex.disk.rest;

//import android.net.SSLCertificateSocketFactory;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

public class HttpClient extends UrlConnectionClient {

    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 30 * 1000;
    private static final int WRITE_TIMEOUT_MILLIS = 30 * 1000;

    private final OkUrlFactory urlFactory;
    private final OkHttpClient client;

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
//        client.setSslSocketFactory(SSLCertificateSocketFactory.getDefault(CONNECT_TIMEOUT_MILLIS, null));

        return client;
    }

    public HttpClient() {
        this(makeClient());
    }

    public HttpClient(OkHttpClient client) {
        this.urlFactory = new OkUrlFactory(client);
        this.client = client;
    }

    public OkHttpClient getClient() {
        return client;
    }

    @Override
    protected HttpURLConnection openConnection(Request request)
            throws IOException {
        return urlFactory.open(new URL(request.getUrl()));
    }
}