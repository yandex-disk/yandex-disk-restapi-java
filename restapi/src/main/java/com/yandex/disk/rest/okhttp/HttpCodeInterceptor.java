package com.yandex.disk.rest.okhttp;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HttpCodeInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isSuccessful()) {
            return response.newBuilder()
                    .addHeader("Y-Code", String.valueOf(response.code()))
                    .addHeader("Y-Message", response.message())
                    .build();
        } else {
            return response;
        }
    }
}
