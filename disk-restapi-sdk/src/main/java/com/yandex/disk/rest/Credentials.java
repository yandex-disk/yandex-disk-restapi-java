/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
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
