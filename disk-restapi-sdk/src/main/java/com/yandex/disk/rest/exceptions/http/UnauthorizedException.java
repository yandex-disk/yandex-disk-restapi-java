package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class UnauthorizedException extends HttpCodeException {
    public UnauthorizedException(int code, ApiError response) {
        super(code, response);
    }
}
