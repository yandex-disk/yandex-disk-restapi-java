/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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