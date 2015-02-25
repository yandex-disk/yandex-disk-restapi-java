package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class ConflictException extends HttpCodeException {
    public ConflictException(int code, ApiError response) {
        super(code, response);
    }
}
