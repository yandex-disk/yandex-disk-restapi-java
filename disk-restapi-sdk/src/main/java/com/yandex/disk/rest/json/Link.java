/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#link">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#link">russian</a></p>
 */
public class Link {

    public enum HttpStatus {
        done, inProgress, error
    }

    HttpStatus httpStatus;

    public final static Link DONE = new Link() {
        {
            httpStatus = HttpStatus.done;
        }
    };

    @SerializedName("href")
    String href;

    @SerializedName("method")
    String method;

    @SerializedName("templated")
    boolean templated;

    public String getHref() {
        return href;
    }

    public String getMethod() {
        return method;
    }

    public boolean isTemplated() {
        return templated;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "Link{" +
                "href='" + href + '\'' +
                ", method='" + method + '\'' +
                ", templated=" + templated +
                ", httpStatus=" + httpStatus +
                '}';
    }
}
