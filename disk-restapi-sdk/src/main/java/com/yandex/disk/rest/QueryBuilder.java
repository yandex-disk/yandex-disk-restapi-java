/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/* package */ class QueryBuilder {

    private static final String UTF8 = "UTF-8";

    private final Map<String, Object> queryMap;
    private final String url;

    /* package */ QueryBuilder(String url) {
        this.url = url;
        this.queryMap = new LinkedHashMap<>();
    }

    /* package */ String build() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(encode(entry.getKey()))
                        .append("=")
                        .append(encode(value.toString()));
            }
        }
        return url + "?" + sb.toString();
    }

    private static String encode(String key) {
        try {
            return URLEncoder.encode(key, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /* package */ QueryBuilder add(String key, String value) {
        queryMap.put(key, value);
        return this;
    }

    /* package */ QueryBuilder add(String key, Boolean value) {
        queryMap.put(key, value);
        return this;
    }

    /* package */ QueryBuilder add(String key, Integer value) {
        queryMap.put(key, value);
        return this;
    }
}
