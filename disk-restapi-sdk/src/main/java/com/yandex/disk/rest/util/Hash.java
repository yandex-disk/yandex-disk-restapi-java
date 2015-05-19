/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    private enum HashType {

        MD5("MD5"), SHA256("SHA-256");

        private final String value;

        HashType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private final String md5, sha256;
    private final long size;

    public Hash(String md5, String sha256, long size) {
        this.md5 = md5;
        this.sha256 = sha256;
        this.size = size;
    }

    public static Hash getHash(File file)
            throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            return getHash(is, file.length());
        } finally {
            close(is);
        }
    }

    private static Hash getHash(InputStream is, long size)
            throws IOException {
        MessageDigest md5Digest, sha256Digest;
        try {
            md5Digest = MessageDigest.getInstance(HashType.MD5.getValue());
            sha256Digest = MessageDigest.getInstance(HashType.SHA256.getValue());
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        byte[] buf = new byte[8192];
        int count;
        while ((count = is.read(buf)) > 0) {
            md5Digest.update(buf, 0, count);
            sha256Digest.update(buf, 0, count);
        }
        return new Hash(toString(md5Digest.digest()), toString(sha256Digest.digest()), size);
    }

    private static void close(InputStream is)
            throws IOException {
        if (is != null) {
            is.close();
        }
    }

    public String getMd5() {
        return md5;
    }

    public String getSha256() {
        return sha256;
    }

    public long getSize() {
        return size;
    }

    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            String n = Integer.toHexString(b & 0x000000FF);
            if (n.length() == 1) {
                out.append('0');
            }
            out.append(n);
        }
        return out.toString();
    }

    @Override
    public String toString() {
        return "Hash{" +
                "md5='" + md5 + '\'' +
                ", sha256='" + sha256 + '\'' +
                ", size=" + size +
                '}';
    }
}
