package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class BadGatewayException extends HttpCodeException {
    public BadGatewayException(int code, ApiError response) {
        super(code, response);
    }
}
