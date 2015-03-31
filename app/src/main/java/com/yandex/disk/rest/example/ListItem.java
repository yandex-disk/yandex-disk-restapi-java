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

package com.yandex.disk.rest.example;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.disk.rest.json.Resource;

public class ListItem implements Parcelable {

    private String name, path, etag, contentType, publicUrl, mediaType;
    private boolean dir;
    private long contentLength, lastUpdated;

    public ListItem(Resource resource) {
        this.name = resource.getName();
        this.path = resource.getPath() != null ? resource.getPath().getPath() : null;  // Must throw an exception in real life code
        this.etag = resource.getMd5();
        this.contentType = resource.getMimeType();
        this.publicUrl = resource.getPublicUrl();
        this.mediaType = resource.getMediaType();
        this.dir = resource.isDir();
        this.contentLength = resource.getSize();
        this.lastUpdated = resource.getModified() != null ? resource.getModified().getTime() : 0;
    }

    private ListItem(String path, String name, long contentLength, long lastUpdated, boolean dir,
                     String etag, String contentType, String publicUrl, String mediaType) {
        this.path = path;
        this.name = name;
        this.contentLength = contentLength;
        this.lastUpdated = lastUpdated;
        this.dir = dir;
        this.etag = etag;
        this.contentType = contentType;
        this.publicUrl = publicUrl;
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", etag='" + etag + '\'' +
                ", contentType='" + contentType + '\'' +
                ", publicUrl='" + publicUrl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", dir=" + dir +
                ", contentLength=" + contentLength +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeLong(contentLength);
        parcel.writeLong(lastUpdated);
        parcel.writeByte((byte) (dir ? 1 : 0));
        parcel.writeString(etag);
        parcel.writeString(contentType);
        parcel.writeString(publicUrl);
        parcel.writeString(mediaType);
    }

    public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {

        public ListItem createFromParcel(Parcel parcel) {
            return new ListItem(parcel.readString(), parcel.readString(), parcel.readLong(),
                    parcel.readLong(), parcel.readByte() > 0, parcel.readString(),
                    parcel.readString(), parcel.readString(), parcel.readString());
        }

        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public long getContentLength() {
        return contentLength;
    }

    /**
     * @return time in milliseconds
     */
    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isDir() {
        return dir;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEtag() {
        return etag;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

}
