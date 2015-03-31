/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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