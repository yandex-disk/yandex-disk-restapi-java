package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#operation">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#operation">russian</a></p>
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