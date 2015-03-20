/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.example;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.OkHttpClientFactory;
import com.yandex.disk.rest.RestClient;

public class RestClientUtil {

    public static RestClient getInstance(final Credentials credentials) {
        OkHttpClient client = OkHttpClientFactory.makeClient();
        client.networkInterceptors().add(new StethoInterceptor());
        return new RestClient(credentials, client);
    }
}
