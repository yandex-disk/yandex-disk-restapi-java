/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

public class ApiVersion {

    @SerializedName("build")
    String build;

    @SerializedName("api_version")
    String apiVersion;

    public String getBuild() {
        return build;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public String toString() {
        return "ApiVersion{" +
                "build='" + build + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }
}
