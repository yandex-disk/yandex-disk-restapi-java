/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.exceptions;

public class UserNotInitializedException extends ServerException {

    /**
     * 403: User have to check http://disk.yandex.ru once before start using this client
     */
    public UserNotInitializedException(String detailMessage) {
        super(detailMessage);
    }
}
