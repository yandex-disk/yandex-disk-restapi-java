package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <a href="http://api.yandex.ru/disk/api/reference/response-objects.xml">API reference</a>
 */
public class Resource {

    @SerializedName("public_key")
    String public_key;

    @SerializedName("_embedded")
    ResourceList resourceList;

    @SerializedName("name")
    String name;

    @SerializedName("created")
    String created;

    @SerializedName("public_url")
    String public_url;

    @SerializedName("origin_path")
    String origin_path;

    @SerializedName("modified")
    String modified;

    @SerializedName("path")
    String path;

    @SerializedName("md5")
    String md5;

    @SerializedName("type")
    String type;

    @SerializedName("mime_type")
    String mime_type;

    @SerializedName("size")
    int size;

    public String getPublicKey() {
        return public_key;
    }

    public ResourceList getItems() {
        return resourceList;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }

    public String getPublicUrl() {
        return public_url;
    }

    public String getOriginPath() {
        return origin_path;
    }

    public String getModified() {
        return modified;
    }

    public String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public String getType() {
        return type;
    }

    public String getMimeType() {
        return mime_type;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "public_key='" + public_key + '\'' +
                ", resourceList(_embedded)=" + resourceList +
                ", name='" + name + '\'' +
                ", created='" + created + '\'' +
                ", public_url='" + public_url + '\'' +
                ", origin_path='" + origin_path + '\'' +
                ", modified='" + modified + '\'' +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                ", type='" + type + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", size=" + size +
                '}';
    }
}
