/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

import java.io.IOException;

public class WebdavIOException extends WebdavException {

    public WebdavIOException(String message) {
        super(message);
    }

    public WebdavIOException(Throwable e) {
        super(e);
    }

    public WebdavIOException(String message, IOException e) {
        super(message, e);
    }

}
