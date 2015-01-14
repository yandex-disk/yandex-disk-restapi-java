/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public abstract class ServerException extends Exception {

    public ServerException() {
        super();
    }

    public ServerException(String detailMessage) {
        super(detailMessage);
    }

    public ServerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }
}
