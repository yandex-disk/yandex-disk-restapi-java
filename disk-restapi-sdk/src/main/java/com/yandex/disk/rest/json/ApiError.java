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