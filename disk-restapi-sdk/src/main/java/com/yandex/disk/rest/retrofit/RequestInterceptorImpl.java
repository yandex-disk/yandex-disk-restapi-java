/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.CustomHeader;

import java.util.List;

import retrofit.RequestInterceptor;

public class RequestInterceptorImpl implements RequestInterceptor {

    private final List<CustomHeader> headers;

    public RequestInterceptorImpl(final List<CustomHeader> headers) {
        this.headers = headers;
    }

    @Override
    public void intercept(final RequestFacade request) {
        for (CustomHeader header : headers) {
            addHeader(request, header);
        }
    }

    private static void addHeader(final RequestFacade request, final CustomHeader header) {
        request.addHeader(header.getName(), header.getValue());
    }
}