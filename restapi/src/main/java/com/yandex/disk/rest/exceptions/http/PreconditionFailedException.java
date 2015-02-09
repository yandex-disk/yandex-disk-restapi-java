package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class PreconditionFailedException extends HttpCodeException {
    public PreconditionFailedException(int code, ApiError response) {
        super(code, response);
    }
}
