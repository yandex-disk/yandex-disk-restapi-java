/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import retrofit.mime.TypedOutput;

public class ResourcesArgs {

    public enum Sort {
        name, path, created, modified, size, deleted
    }

    private String path, fields, sort, previewSize, publicKey, mediaType;
    private Integer limit, offset;
    private Boolean previewCrop;
    private ResourcesHandler parsingHandler;
    private TypedOutput body;

    private ResourcesArgs(String path, String fields, String sort, String previewSize,
                          Integer limit, Integer offset, Boolean previewCrop, String publicKey,
                          String mediaType, TypedOutput body, ResourcesHandler parsingHandler) {
        this.path = path;
        this.fields = fields;
        this.sort = sort;
        this.previewSize = previewSize;
        this.limit = limit;
        this.offset = offset;
        this.previewCrop = previewCrop;
        this.publicKey = publicKey;
        this.mediaType = mediaType;
        this.body = body;
        this.parsingHandler = parsingHandler;
    }

    public String getPath() {
        return path;
    }

    public String getFields() {
        return fields;
    }

    public String getSort() {
        return sort;
    }

    public String getPreviewSize() {
        return previewSize;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Boolean getPreviewCrop() {
        return previewCrop;
    }

    public TypedOutput getBody() {
        return body;
    }

    public ResourcesHandler getParsingHandler() {
        return parsingHandler;
    }

    @Override
    public String toString() {
        return "ResourcesArgs{" +
                "path='" + path + '\'' +
                ", fields='" + fields + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", sort='" + sort + '\'' +
                ", previewSize='" + previewSize + '\'' +
                ", previewCrop=" + previewCrop +
                ", publicKey=" + publicKey +
                ", mediaType=" + mediaType +
                ", body=" + body +
                ", parsingHandler=" + (parsingHandler != null) +
                '}';
    }

    public static class Builder {
        private String path, fields, sort, previewSize, publicKey, mediaType;
        private Integer limit, offset;
        private Boolean previewCrop;
        private ResourcesHandler parsingHandler;
        private TypedOutput body;

        public ResourcesArgs build() {
            return new ResourcesArgs(path, fields, sort, previewSize, limit, offset, previewCrop,
                    publicKey, mediaType, body, parsingHandler);
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setFields(String fields) {
            this.fields = fields;
            return this;
        }

        public Builder setSort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder setSort(Sort sort) {
            this.sort = sort.name();
            return this;
        }

        public Builder setPreviewSize(String previewSize) {
            this.previewSize = previewSize;
            return this;
        }

        public Builder setLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder setOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder setPreviewCrop(Boolean previewCrop) {
            this.previewCrop = previewCrop;
            return this;
        }

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setMediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setParsingHandler(ResourcesHandler parsingHandler) {
            this.parsingHandler = parsingHandler;
            return this;
        }

        public Builder setBody(TypedOutput body) {
            this.body = body;
            return this;
        }
    }
}
