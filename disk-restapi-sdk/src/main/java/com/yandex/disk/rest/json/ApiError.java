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
 * @see <a href="http://api.yandex.ru/disk/api/reference/response-objects.xml">API reference</a>
 */
public class ApiError {

    @SerializedName("description")
    String description;

    @SerializedName("error")
    String error;

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "description='" + description + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}