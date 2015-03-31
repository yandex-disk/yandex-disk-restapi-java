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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Credentials {

    /* package */ static final String AUTHORIZATION_HEADER = "Authorization";
    /* package */ static final String USER_AGENT_HEADER = "User-Agent";
    private static final String USER_AGENT = "Cloud API Android Client Example/1.0";

    protected String user, token;

    public Credentials(final String user, final String token) {
        this.user = user;
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public List<CustomHeader> getHeaders() {
        List<CustomHeader> list = new ArrayList<>();
        list.add(new CustomHeader(USER_AGENT_HEADER, USER_AGENT));
        list.add(new CustomHeader(AUTHORIZATION_HEADER, "OAuth " + getToken()));
        return Collections.unmodifiableList(list);
    }
}
