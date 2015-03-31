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
