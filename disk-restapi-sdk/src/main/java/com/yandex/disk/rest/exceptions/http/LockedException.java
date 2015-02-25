package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class LockedException extends HttpCodeException {
    public LockedException(int code, ApiError response) {
        super(code, response);
    }
}
