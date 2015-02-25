package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class ForbiddenException extends HttpCodeException {
    public ForbiddenException(int code, ApiError response) {
        super(code, response);
    }
}
