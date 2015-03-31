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

    /* package */ String build()
            throws UnsupportedEncodingException {
        return build(UTF8);
    }

    /* package */ String build(final String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), encoding))
                        .append("=")
                        .append(URLEncoder.encode(value.toString(), encoding));
            }
        }
        return url + "?" + sb.toString();
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
