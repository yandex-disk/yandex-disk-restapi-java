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
