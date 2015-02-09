package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class ServiceUnavailableException extends HttpCodeException {
    public ServiceUnavailableException(int code, ApiError response) {
        super(code, response);
    }
}
