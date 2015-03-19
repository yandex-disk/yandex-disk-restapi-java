/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class InsufficientStorageException extends HttpCodeException {
    public InsufficientStorageException(int code, ApiError response) {
        super(code, response);
    }
}
