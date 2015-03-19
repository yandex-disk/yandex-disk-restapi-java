/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

import java.io.IOException;

/**
 * {@link retrofit.RetrofitError.Kind#NETWORK}
 */
public class NetworkIOException extends IOException {
    public NetworkIOException(Throwable ex) {
        super(ex);
    }
}
