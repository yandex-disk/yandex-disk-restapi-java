package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class MethodNotAllowedException extends HttpCodeException {
    public MethodNotAllowedException(int code, ApiError response) {
        super(code, response);
    }
}
