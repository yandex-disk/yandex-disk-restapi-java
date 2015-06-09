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
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#operation">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#operation">russian</a></p>
 */
public class Operation {

    private static final String IN_PROGRESS = "in-progress";
    private static final String SUCCESS = "success";

    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public boolean isInProgress() {
        return IN_PROGRESS.equals(status);
    }

    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "status='" + status + '\'' +
                '}';
    }
}