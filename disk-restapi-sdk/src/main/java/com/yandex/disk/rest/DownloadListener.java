/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.DownloadNoSpaceAvailableException;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DownloadListener implements ProgressListener {

    /**
     * Local file length for resuming download. 0 if local file not exist
     */
    public long getLocalLength() {
        return 0;
    }

    /**
     * Used for <tt>If-None-Match</tt> or <tt>If-Range</tt>. MD5 or <tt>null</tt> if not applicable or not known
     * @see <a href="http://tools.ietf.org/html/rfc2616#page-132">rfc 2616</a>
     */
    public String getETag() {
        return null;
    }

    /**
     * Start position after server response
     */
    public void setStartPosition(long position) {
    }

    /**
     * Content length after server response. 0 if not known
     * @throws DownloadNoSpaceAvailableException if no local space for content
     */
    public void setContentLength(long length)
        throws DownloadNoSpaceAvailableException {
    }

    public abstract OutputStream getOutputStream(boolean append)
            throws IOException;

    @Override
    public void updateProgress(long loaded, long total) {
    }

    @Override
    public boolean hasCancelled() {
        return false;
    }

    public void setETag(String etag) {
    }

    public void setContentType(String contentType) {
    }
}
