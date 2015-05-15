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
 * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/response-objects.xml#link">english</a>,
 * <a href="https://tech.yandex.ru/disk/api/reference/response-objects-docpage/#link">russian</a></p>
 */
public class Link {

    public enum HttpStatus {
        done, inProgress, error
    }

    HttpStatus httpStatus;

    public final static Link DONE = new Link() {
        {
            httpStatus = HttpStatus.done;
        }
    };

    @SerializedName("href")
    String href;

    @SerializedName("method")
    String method;

    @SerializedName("templated")
    boolean templated;

    public String getHref() {
        return href;
    }

    public String getMethod() {
        return method;
    }

    public boolean isTemplated() {
        return templated;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "Link{" +
                "href='" + href + '\'' +
                ", method='" + method + '\'' +
                ", templated=" + templated +
                ", httpStatus=" + httpStatus +
                '}';
    }
}
