/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;
import com.yandex.disk.rest.util.ISO8601;
import com.yandex.disk.rest.util.ResourcePath;

import java.util.Date;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#resource">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#resource">russian</a></p>
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

    @SerializedName("media_type")
    String mediaType;

    @SerializedName("preview")
    String preview;

    @SerializedName("size")
    long size;

    @SerializedName("custom_properties")
    Object properties;

    public String getPublicKey() {
        return publicKey;
    }

    public ResourceList getItems() {
        return resourceList;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created != null ? ISO8601.parse(created) : null;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public ResourcePath getOriginPath() {
        return originPath != null ? new ResourcePath(originPath) : null;
    }

    public Date getModified() {
        return modified != null ? ISO8601.parse(modified) : null;
    }

    public ResourcePath getPath() {
        return path != null ? new ResourcePath(path) : null;
    }

    public String getMd5() {
        return md5;
    }

    public String getType() {
        return type;
    }

    public boolean isDir() {
        return "dir".equalsIgnoreCase(type);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getPreview() {
        return preview;
    }

    public long getSize() {
        return size;
    }

    public Object getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "publicKey='" + publicKey + '\'' +
                ", resourceList=" + resourceList +
                ", name='" + name + '\'' +
                ", created='" + getCreated() + '\'' +
                ", publicUrl='" + publicUrl + '\'' +
                ", originPath='" + getOriginPath() + '\'' +
                ", modified='" + getModified() + '\'' +
                ", path='" + getPath() + '\'' +
                ", md5='" + md5 + '\'' +
                ", type='" + type + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", preview='" + preview + '\'' +
                ", size=" + size +
                ", properties=" + properties +
                '}';
    }
}
