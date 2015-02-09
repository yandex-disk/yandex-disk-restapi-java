package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class UnsupportedMediaTypeException extends HttpCodeException {
    public UnsupportedMediaTypeException(int code, ApiError response) {
        super(code, response);
    }
}
