/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class LockedException extends HttpCodeException {
    public LockedException(int code, ApiError response) {
        super(code, response);
    }
}
