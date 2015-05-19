/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.exceptions.http;

/**
 * 304 on GET with If-None-Match
 */
public class FileNotModifiedException extends HttpCodeException {
    public FileNotModifiedException(int code) {
        super(code);
    }
}
