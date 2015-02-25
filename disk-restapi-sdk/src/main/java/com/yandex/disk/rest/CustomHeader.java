
package com.yandex.disk.rest;

public class CustomHeader {

    private final String name, value;

    public CustomHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
