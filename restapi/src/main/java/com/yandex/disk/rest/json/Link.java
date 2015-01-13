package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#link">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#link">russian</a></p>
 */
public class Link {

    @SerializedName("href")
    String href;

    @SerializedName("method")
    String method;

    @SerializedName("templated")
    boolean templated;

    public String getHref() {
        return href;
    }

    /**
     * Pure magic :(
     *
     * <br/>TODO ask the server to change API
     */
    @Deprecated
    public String getOperationId() {
        return href != null ? href.substring(href.length() - 64, href.length()) : null;
    }

    public String getMethod() {
        return method;
    }

    public boolean isTemplated() {
        return templated;
    }

    @Override
    public String toString() {
        return "Link{" +
                "href='" + href + '\'' +
                ", method='" + method + '\'' +
                ", templated=" + templated +
                '}';
    }
}
