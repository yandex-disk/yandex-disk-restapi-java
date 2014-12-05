package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DiskMeta {

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
        return "DiskMeta{" +
                "trashSize=" + trashSize +
                ", totalSpace=" + totalSpace +
                ", usedSpace=" + usedSpace +
                ", systemFolders=" + systemFolders +
                '}';
    }
}
