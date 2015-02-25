package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class TooManyRequestsException extends HttpCodeException {
    public TooManyRequestsException(int code, ApiError response) {
        super(code, response);
    }
}
