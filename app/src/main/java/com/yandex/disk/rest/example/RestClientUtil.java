/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.example;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.yandex.disk.rest.HttpClient;
import com.yandex.disk.rest.RestClient;

public class RestClientUtil {

    public static RestClient getInstance(final Credentials credentials) {
        HttpClient client = new HttpClient();
        client.getClient().networkInterceptors().add(new StethoInterceptor());
        return new RestClient(credentials, client);
    }
}
