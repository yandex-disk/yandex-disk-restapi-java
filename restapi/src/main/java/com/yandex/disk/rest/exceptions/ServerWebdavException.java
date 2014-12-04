/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public class ServerWebdavException extends WebdavException {

    public ServerWebdavException(String detailMessage) {
        super(detailMessage);
    }

    public ServerWebdavException() {
        super();
    }

}
