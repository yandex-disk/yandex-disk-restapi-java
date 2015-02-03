package com.yandex.disk.rest.util;

public class ResourcePath {

    private final static String SEPARATOR = ":";

    private final String prefix, path;

    public ResourcePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        String[] arr = str.split(SEPARATOR);    // TODO search first ':' instead of split
        if (arr.length != 2) {
            if (arr.length < 2) {
                prefix = null;
                path = str;
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            prefix = arr[0];
            path = arr[1];
            if (prefix == null || path == null || prefix.length() == 0 || path.length() == 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    public ResourcePath(String prefix, String path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        this.prefix = prefix;
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourcePath that = (ResourcePath) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return prefix + SEPARATOR + path;
    }
}
