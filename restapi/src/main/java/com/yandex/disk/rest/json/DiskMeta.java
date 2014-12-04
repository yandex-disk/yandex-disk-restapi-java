package com.yandex.disk.rest.json;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DiskMeta {

    @SerializedName("trash_size")
    long trash_size;

    @SerializedName("total_space")
    long total_space;

    @SerializedName("used_space")
    long used_space;

    @SerializedName("system_folders")
    Map<String,String> system_folders;

    @Override
    public String toString() {
        return "DiskMeta{" +
                "trash_size=" + trash_size +
                ", total_space=" + total_space +
                ", used_space=" + used_space +
                ", system_folders=" + system_folders +
                '}';
    }
}
