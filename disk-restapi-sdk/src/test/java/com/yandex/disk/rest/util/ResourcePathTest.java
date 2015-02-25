package com.yandex.disk.rest.util;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ResourcePathTest {

    @Test
    public void testPositive() throws Exception {
        String src = "prefix:/long/path";
        ResourcePath resourcePath = new ResourcePath(src);
        assertTrue("prefix".equals(resourcePath.getPrefix()));
        assertTrue("/long/path".equals(resourcePath.getPath()));
        assertTrue(src.equals(resourcePath.toString()));
        assertEquals(resourcePath, new ResourcePath("prefix", "/long/path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArg() throws Exception {
        new ResourcePath(null);
    }

    @Ignore @Test(expected = IllegalArgumentException.class)
    public void testNullArg2() throws Exception {
        new ResourcePath(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArg3() throws Exception {
        new ResourcePath("", null);
    }

    @Ignore @Test(expected = IllegalArgumentException.class)
    public void testNoPrefix() throws Exception {
        new ResourcePath("/long/path");
    }

    @Ignore @Test(expected = IllegalArgumentException.class)
    public void testNoPath() throws Exception {
        new ResourcePath("1:");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPrefix() throws Exception {
        new ResourcePath(":2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecondColon() throws Exception {
        new ResourcePath("1:2:3");
    }
}
