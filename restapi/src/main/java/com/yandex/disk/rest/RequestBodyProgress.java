package com.yandex.disk.rest;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;
import com.yandex.disk.rest.exceptions.CancelledUploadingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public abstract class RequestBodyProgress {

    private static final int SIZE = 2048;

    /**
     * Returns a new request body that transmits the content of {@code file}.
     * <br/>
     * Based on {@link RequestBody#create(com.squareup.okhttp.MediaType, java.io.File)}
     *
     * @see RequestBody#create(com.squareup.okhttp.MediaType, java.io.File)
     */
    public static RequestBody create(final MediaType contentType, final File file, final long startOffset,
                                     final ProgressListener listener) {
        if (file == null) {
            throw new NullPointerException("content == null");
        }

        if (listener == null && startOffset == 0) {
            return RequestBody.create(contentType, file);
        }

        return new RequestBody() {

            private void updateProgress(long loaded)
                    throws CancelledUploadingException {
                if (listener.hasCancelled()) {
                    throw new CancelledUploadingException();
                }
                listener.updateProgress(loaded + startOffset, file.length());
            }

            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length() - startOffset;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                InputStream inputStream = new FileInputStream(file);
                try {
                    if (startOffset > 0) {
                        long skipped = inputStream.skip(startOffset);
                        if (skipped != startOffset) {
                            throw new IllegalArgumentException("inputStream.skip() failed"); // TODO XXX
                        }
                    }
                    long loaded = 0;
                    updateProgress(loaded);
                    source = Okio.source(inputStream);
                    Buffer buffer = new Buffer();
                    for (long readCount; (readCount = source.read(buffer, SIZE)) != -1; ) {
                        sink.write(buffer, readCount);
//                        sink.emitCompleteSegments(); TODO XXX
                        loaded += readCount;
                        updateProgress(loaded);
                    }
                    Log.d("loaded: " + loaded);
                } finally {
                    Util.closeQuietly(source);
                    Util.closeQuietly(inputStream);
                }
            }
        };
    }
}
