package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class NotAcceptableException extends HttpCodeException {
    public NotAcceptableException(int code, ApiError response) {
        super(code, response);
    }
}
