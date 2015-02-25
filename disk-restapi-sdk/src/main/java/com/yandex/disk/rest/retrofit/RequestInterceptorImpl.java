package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.CustomHeader;

import java.util.List;

import retrofit.RequestInterceptor;

public class RequestInterceptorImpl implements RequestInterceptor {

    private final List<CustomHeader> commonHeaders, headerList;

    public RequestInterceptorImpl(final List<CustomHeader> commonHeaders, final List<CustomHeader> headerList) {
        this.commonHeaders = commonHeaders;
        this.headerList = headerList;
    }

    @Override
    public void intercept(final RequestFacade request) {
        for (CustomHeader header : commonHeaders) {
            addHeader(request, header);
        }
        if (headerList != null) {
            for (CustomHeader header : headerList) {
                addHeader(request, header);
            }
        }
    }

    private static void addHeader(final RequestFacade request, final CustomHeader header) {
        request.addHeader(header.getName(), header.getValue());
    }
}