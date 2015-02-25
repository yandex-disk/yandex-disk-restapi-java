
package com.yandex.disk.rest.exceptions.http;

/**
 * 304 on GET with If-None-Match
 */
public class FileNotModifiedException extends HttpCodeException {
    public FileNotModifiedException(int code) {
        super(code);
    }
}
