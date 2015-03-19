/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public class ServerIOException extends ServerException {

    public ServerIOException() {
        super();
    }

    public ServerIOException(String message) {
        super(message);
    }

    public ServerIOException(Throwable e) {
        super(e);
    }
}
