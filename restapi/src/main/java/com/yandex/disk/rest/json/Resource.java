package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * @see <a href="http://api.yandex.ru/disk/api/reference/response-objects.xml">API reference</a>
 */
public class Resource {

    @SerializedName("public_key")
    String publicKey;

    @SerializedName("_embedded")
    ResourceList resourceList;

    @SerializedName("name")
    String name;

    @SerializedName("created")
    String created;

    @SerializedName("public_url")
    String publicUrl;

    @SerializedName("origin_path")
    String originPath;

    @SerializedName("modified")
    String modified;

    @SerializedName("path")
    String path;

    @SerializedName("md5")
    String md5;

    @SerializedName("type")
    String type;

    @SerializedName("mime_type")
    String mimeType;

    @SerializedName("size")
    int size;

    public String getPublicKey() {
        return publicKey;
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
        return publicUrl;
    }

    public String getOriginPath() {
        return originPath;
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
        return mimeType;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "publicKey='" + publicKey + '\'' +
                ", resourceList=" + resourceList +
                ", name='" + name + '\'' +
                ", created='" + created + '\'' +
                ", publicUrl='" + publicUrl + '\'' +
                ", originPath='" + originPath + '\'' +
                ", modified='" + modified + '\'' +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                ", type='" + type + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                '}';
    }
}
