/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
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
