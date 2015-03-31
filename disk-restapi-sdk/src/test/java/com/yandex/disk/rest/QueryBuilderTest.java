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
