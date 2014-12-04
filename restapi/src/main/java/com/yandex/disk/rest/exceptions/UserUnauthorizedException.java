package com.yandex.disk.rest.exceptions;

public class UserUnauthorizedException extends WebdavIOException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
