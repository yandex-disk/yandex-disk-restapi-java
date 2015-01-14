package com.yandex.disk.rest.exceptions;

public class PreconditionFailedException extends ServerException {
    public PreconditionFailedException(String message) {
        super(message);
    }
}
