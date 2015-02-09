package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.ApiError;

/**
 * 4xx and 5xx http codes<br/>
 * {@link retrofit.RetrofitError.Kind#HTTP}<br/>
 * <br/>
 * Basic rules:<br/>
 * Replace <tt>Error</tt> in the error name from {@link ApiError#getError()}
 * to <tt>Exception</tt> to get new exception name<br/>
 * New exception must extends <tt>HttpCodeException</tt>
 */
public class HttpCodeException extends ServerIOException /* TODO change to ServerException? */ {

    protected final int code;
    protected final ApiError response;

    public HttpCodeException(int code, ApiError response) {
        super();
        this.code = code;
        this.response = response;
    }

    public HttpCodeException(int code) {
        this(code, null);
    }

    public int getCode() {
        return code;
    }

    public ApiError getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "HttpCodeException{" +
                "code=" + code +
                ", response=" + response +
                '}';
    }
}
