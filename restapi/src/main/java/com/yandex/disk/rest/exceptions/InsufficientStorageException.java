/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public class InsufficientStorageException extends ServerWebdavException {

    public InsufficientStorageException() {
        super("The server is unable to store the representation needed to complete the request");
    }

}
