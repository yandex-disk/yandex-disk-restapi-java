/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class OkHttpClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpClientFactory.class);

    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 30 * 1000;
    private static final int WRITE_TIMEOUT_MILLIS = 30 * 1000;

    public static OkHttpClient makeClient() {
        OkHttpClient client = new OkHttpClient();

        if (logger.isDebugEnabled()) {
            client.networkInterceptors().add(new LoggingInterceptor());
        }

        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        client.setFollowSslRedirects(true);
        client.setFollowRedirects(true);

        return client;
    }
}
