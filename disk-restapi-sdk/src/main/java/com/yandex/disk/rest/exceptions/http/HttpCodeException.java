/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

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
public class HttpCodeException extends ServerIOException {

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
