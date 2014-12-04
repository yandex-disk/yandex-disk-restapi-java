package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <a href="http://api.yandex.ru/disk/api/reference/response-objects.xml">API reference</a>
 */
public class Operation {

    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "status='" + status + '\'' +
                '}';
    }
}