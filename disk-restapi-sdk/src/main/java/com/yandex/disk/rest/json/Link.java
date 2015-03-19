/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
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

    public final static Link IN_PROGRESS = new Link() {
        {
            httpStatus = HttpStatus.inProgress;
        }
    };

    public final static Link ERROR = new Link() {
        {
            httpStatus = HttpStatus.error;
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
