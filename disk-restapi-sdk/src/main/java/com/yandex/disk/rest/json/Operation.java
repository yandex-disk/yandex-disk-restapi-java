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

/**
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#operation">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#operation">russian</a></p>
 */
public class Operation {

    private static final String IN_PROGRESS = "in-progress";
    private static final String SUCCESS = "success";

    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public boolean isInProgress() {
        return IN_PROGRESS.equals(status);
    }

    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "status='" + status + '\'' +
                '}';
    }
}