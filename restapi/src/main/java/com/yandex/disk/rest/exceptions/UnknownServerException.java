/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public class UnknownServerException extends ServerException {
    public UnknownServerException(Exception ex) {
        super(ex);
    }

    public UnknownServerException(String message) {
        super(message);
    }
}
