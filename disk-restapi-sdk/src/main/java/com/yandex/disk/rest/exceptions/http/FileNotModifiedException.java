/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions.http;

/**
 * 304 on GET with If-None-Match
 */
public class FileNotModifiedException extends HttpCodeException {
    public FileNotModifiedException(int code) {
        super(code);
    }
}
