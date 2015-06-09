/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
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