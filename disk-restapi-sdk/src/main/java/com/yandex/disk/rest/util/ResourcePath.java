/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.util;

public class ResourcePath {

    private final static char SEPARATOR = ':';

    private final String prefix, path;

    public ResourcePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        int index = str.indexOf(SEPARATOR);
        if (index == -1) {
            prefix = null;
            path = str;
        } else {
            prefix = str.substring(0, index);
            path = str.substring(index + 1);
            if (prefix.length() == 0) {
                throw new IllegalArgumentException();
            }
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException();
        }
    }

    public ResourcePath(String prefix, String path) {
        if (prefix == null || path == null || prefix.length() == 0 || path.length() == 0) {
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
        return (prefix != null ? prefix + SEPARATOR : "") + path;
    }
}
