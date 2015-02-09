package com.yandex.disk.rest.exceptions.http;

public class RangeNotSatisfiableException extends HttpCodeException {

    public RangeNotSatisfiableException(int code) {
        super(code);
    }
}
