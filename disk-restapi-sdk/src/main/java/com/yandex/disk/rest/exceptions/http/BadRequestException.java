package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class BadRequestException extends HttpCodeException {
    public BadRequestException(int code, ApiError response) {
        super(code, response);
    }
}
