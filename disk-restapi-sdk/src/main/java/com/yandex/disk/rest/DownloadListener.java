/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
