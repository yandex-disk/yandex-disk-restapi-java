package com.yandex.disk.rest.exceptions;

import java.io.IOException;

/**
 * {@link retrofit.RetrofitError.Kind#NETWORK}
 */
public class NetworkIOException extends IOException {
    public NetworkIOException(String message) {
        super(message);
    }
}
