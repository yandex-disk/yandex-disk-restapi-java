package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @see <a href="http://api.yandex.ru/disk/api/reference/response-objects.xml">API reference</a>
 */
public class ResourceList {

    @SerializedName("sort")
    String sort;

    @SerializedName("public_key")
    String public_key;

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
        return public_key;
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
                ", public_key='" + public_key + '\'' +
                ", items=" + items +
                ", path='" + path + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", total=" + total +
                '}';
    }
}