/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.exceptions;

/**
 * {@link retrofit.RetrofitError.Kind#CONVERSION}
 */
public class RetrofitConversionException extends ServerIOException {
    public RetrofitConversionException(Throwable ex) {
        super(ex);
    }
}
