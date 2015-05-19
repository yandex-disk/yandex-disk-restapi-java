/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
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