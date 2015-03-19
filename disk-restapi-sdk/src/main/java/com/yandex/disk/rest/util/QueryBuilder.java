/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryBuilder {

    private static final String UTF8 = "UTF-8";

    private final Map<String, Object> queryMap;
    private final String url;

    public QueryBuilder(String url) {
        this.url = url;
        this.queryMap = new LinkedHashMap<>();
    }

    public String build()
            throws UnsupportedEncodingException {
        return build(UTF8);
    }

    public String build(final String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,Object> entry : queryMap.entrySet()) {
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

    public QueryBuilder add(String key, String value) {
        queryMap.put(key, value);
        return this;
    }

    public QueryBuilder add(String key, Boolean value) {
        queryMap.put(key, value);
        return this;
    }

    public QueryBuilder add(String key, Integer value) {
        queryMap.put(key, value);
        return this;
    }
}
