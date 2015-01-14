package com.yandex.disk.rest.exceptions;

public class UserUnauthorizedException extends ServerIOException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
