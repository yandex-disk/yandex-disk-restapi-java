/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

/**
 * {@link retrofit.RetrofitError.Kind#CONVERSION}
 */
public class RetrofitConversionException extends ServerIOException {
    public RetrofitConversionException(Throwable ex) {
        super(ex);
    }
}
