/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.exceptions;

public class DownloadNoSpaceAvailableException extends ServerException {

    private final String destinationFolder;
    private final long length;

    public DownloadNoSpaceAvailableException(String destinationFolder, long length) {
        super();
        this.destinationFolder = destinationFolder;
        this.length = length;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public long getLength() {
        return length;
    }
}
