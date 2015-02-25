package com.yandex.disk.rest.exceptions;

/**
 * {@link retrofit.RetrofitError.Kind#CONVERSION}
 */
public class RetrofitConversionException extends ServerIOException {
    public RetrofitConversionException(Throwable ex) {
        super(ex);
    }
}
