/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.yandex.disk.rest.QueryBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class QueryBuilderTest {

    @Test
    public void testQueryBuilder() throws UnsupportedEncodingException {
        String str = new QueryBuilder("https://cloud-api.yandex.net/v1/disk/resources")
                .add("path", "\u20ac\u00a3\u0024")
                .add("name", (Integer) null)
                .add("overwrite", true)
                .build();
        assertTrue(str.equals(
                "https://cloud-api.yandex.net/v1/disk/resources?path=%E2%82%AC%C2%A3%24&overwrite=true"));
        assertTrue(URLDecoder.decode(str, "UTF-8").equals(
                "https://cloud-api.yandex.net/v1/disk/resources?path=\u20ac\u00a3\u0024&overwrite=true"));
    }
}
