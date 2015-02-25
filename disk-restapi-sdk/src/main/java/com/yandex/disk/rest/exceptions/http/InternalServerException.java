package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class InternalServerException extends HttpCodeException {
    public InternalServerException(int code, ApiError response) {
        super(code, response);
    }
}
