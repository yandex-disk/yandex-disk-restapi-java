/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#resourcelist">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#resourcelist">russian</a></p>
 */
public class ResourceList {

    @SerializedName("sort")
    String sort;

    @SerializedName("public_key")
    String publicKey;

    @SerializedName("items")
    List<Resource> items;

    @SerializedName("path")
    String path;

    @SerializedName("limit")
    int limit;

    @SerializedName("offset")
    int offset;

    @SerializedName("total")
    int total;

    public String getSort() {
        return sort;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public List<Resource> getItems() {
        return items;
    }

    public String getPath() {
        return path;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "ResourceList{" +
                "sort='" + sort + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", items=" + items +
                ", path='" + path + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", total=" + total +
                '}';
    }
}