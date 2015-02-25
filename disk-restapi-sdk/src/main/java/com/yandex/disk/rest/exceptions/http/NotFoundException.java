package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class NotFoundException extends HttpCodeException {
    public NotFoundException(int code) {
        super(code);
    }

    public NotFoundException(int code, ApiError response) {
        super(code, response);
    }
}
