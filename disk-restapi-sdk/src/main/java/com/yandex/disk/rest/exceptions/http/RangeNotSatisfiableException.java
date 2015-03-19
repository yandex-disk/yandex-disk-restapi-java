/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions.http;

public class RangeNotSatisfiableException extends HttpCodeException {

    public RangeNotSatisfiableException(int code) {
        super(code);
    }
}
