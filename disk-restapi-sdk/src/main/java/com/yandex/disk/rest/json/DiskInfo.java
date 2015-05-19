/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#disk">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#disk">russian</a></p>
 */
public class DiskInfo {

    @SerializedName("trash_size")
    long trashSize;

    @SerializedName("total_space")
    long totalSpace;

    @SerializedName("used_space")
    long usedSpace;

    @SerializedName("system_folders")
    Map<String,String> systemFolders;

    public long getTrashSize() {
        return trashSize;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public Map<String, String> getSystemFolders() {
        return systemFolders;
    }

    @Override
    public String toString() {
        return "DiskCapacity{" +
                "trashSize=" + trashSize +
                ", totalSpace=" + totalSpace +
                ", usedSpace=" + usedSpace +
                ", systemFolders=" + systemFolders +
                '}';
    }
}
