/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.okhttp;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        logger.debug(String.format("Sending request %s %s%n on %s%n%s",
                request.method(), request.url(), chain.connection(), request.headers()));

        Response src = chain.proceed(request);
        Response response = src.newBuilder()
                .addHeader("Debug-Code", String.valueOf(src.code()))
                .addHeader("Debug-Message", src.message())
                .build();

        long t2 = System.nanoTime();
        logger.debug(String.format("Received response for %s%n in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
