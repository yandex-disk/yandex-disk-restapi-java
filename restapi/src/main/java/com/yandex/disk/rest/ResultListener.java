package com.yandex.disk.rest;

import java.io.InputStream;

public interface ResultListener {
    void parseResult(InputStream inputStream);
}
