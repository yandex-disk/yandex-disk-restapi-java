package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class UnprocessableEntityException extends HttpCodeException {
    public UnprocessableEntityException(int code, ApiError response) {
        super(code, response);
    }
}
