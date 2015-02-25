package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class InsufficientStorageException extends HttpCodeException {
    public InsufficientStorageException(int code, ApiError response) {
        super(code, response);
    }
}
