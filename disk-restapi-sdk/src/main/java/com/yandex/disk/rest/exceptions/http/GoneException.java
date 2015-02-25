package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class GoneException extends HttpCodeException {
    public GoneException(int code, ApiError response) {
        super(code, response);
    }
}
