/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

import java.io.IOException;

public class ServerIOException extends ServerException {

    public ServerIOException(String message) {
        super(message);
    }

    public ServerIOException(Throwable e) {
        super(e);
    }

    public ServerIOException(String message, IOException e) {
        super(message, e);
    }

}
