package com.yandex.disk.rest;

public class ResourcesArgs {

    private String path, fields, sort, previewSize, publicKey;
    private Integer limit, offset;
    private Boolean previewCrop;
    private ResourcesHandler parsingHandler;

    private ResourcesArgs(String path, String fields, String sort, String previewSize,
                          Integer limit, Integer offset, Boolean previewCrop, String publicKey,
                          ResourcesHandler parsingHandler) {
        this.path = path;
        this.fields = fields;
        this.sort = sort;
        this.previewSize = previewSize;
        this.limit = limit;
        this.offset = offset;
        this.previewCrop = previewCrop;
        this.publicKey = publicKey;
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

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Boolean getPreviewCrop() {
        return previewCrop;
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
                ", parsingHandler=" + (parsingHandler != null) +
                '}';
    }

    public static class Builder {
        private String path, fields, sort, previewSize, publicKey;
        private Integer limit, offset;
        private Boolean previewCrop;
        private ResourcesHandler parsingHandler;

        public ResourcesArgs build() {
            return new ResourcesArgs(path, fields, sort, previewSize, limit, offset, previewCrop,
                    publicKey, parsingHandler);
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

        public Builder setParsingHandler(ResourcesHandler parsingHandler) {
            this.parsingHandler = parsingHandler;
            return this;
        }
    }
}
