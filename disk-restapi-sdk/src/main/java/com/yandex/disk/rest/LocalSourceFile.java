package com.yandex.disk.rest;

import com.yandex.disk.rest.util.Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalSourceFile implements SourceFile {

    private final File file;
    private Hash hash;

    public LocalSourceFile(File file) {
        this.file = file;
    }

    @Override
    public Hash getHash() throws IOException {
        if (hash == null) {
            hash = Hash.getHash(file);
        }
        return hash;
    }

    @Override
    public InputStream getInputStream(long startOffset) throws IOException {
        InputStream input = new FileInputStream(file);
        if (startOffset != 0) {
            long skipped = input.skip(startOffset);
            if (skipped != startOffset) {
                throw new IOException("RequestBodyProgress: inputStream.skip() failed");
            }
        }
        return input;
    }

    @Override
    public long getContentSize() {
        return file.length();
    }
}
