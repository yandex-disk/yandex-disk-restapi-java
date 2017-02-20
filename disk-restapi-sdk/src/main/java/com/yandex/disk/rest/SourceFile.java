package com.yandex.disk.rest;

import com.yandex.disk.rest.util.Hash;

import java.io.IOException;
import java.io.InputStream;

public interface SourceFile {

    /**
     * Get hash for content
     * @return
     */
    Hash getHash() throws IOException;

    /**
     * Getinput stream using current offset.
     * For file you should change current position to offset
     * @param startOffset
     * @return input stream for content
     */
    InputStream getInputStream(long startOffset) throws IOException;


    /**
     * Return size of content in bytes.
     * For file it is just size.
     *
     * @return
     */
    long getContentSize();

}
