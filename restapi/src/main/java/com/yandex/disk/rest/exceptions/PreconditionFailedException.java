package com.yandex.disk.rest.exceptions;

public class PreconditionFailedException extends WebdavException {
    public PreconditionFailedException(String message) {
        super(message);
    }
}
