package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class NotImplementedException extends HttpCodeException {
    public NotImplementedException(int code, ApiError response) {
        super(code, response);
    }
}
