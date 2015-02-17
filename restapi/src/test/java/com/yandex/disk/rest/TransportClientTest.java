package com.yandex.disk.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.disk.rest.exceptions.CancelledUploadingException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.WrongMethodException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskCapacity;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;
import com.yandex.disk.rest.util.Hash;
import com.yandex.disk.rest.util.ResourcePath;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import retrofit.converter.GsonConverter;
import retrofit.mime.TypedOutput;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TransportClientTest {

    private static final Logger logger = LoggerFactory.getLogger(TransportClientTest.class);

    private TransportClient client;

    @Before
    public void setUp() throws Exception {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        logger.info("pwd: " + new File(".").getAbsolutePath());

        FileInputStream propertiesFile = new FileInputStream("local.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);

        String user = properties.getProperty("test.user");
        String token = properties.getProperty("test.token");

        assertThat(user, notNullValue());
        assertThat(token, notNullValue());

        Credentials credentials = new CredentialsImpl(user, token);

        client = TransportClient.getInstance(credentials);

        generateResources();
    }

    private void generateResources() throws Exception {
        // TODO move to proper directory
        Runtime.getRuntime()
                .exec("/usr/bin/env dd if=/dev/urandom of=testResources/test-upload-002.bin bs=1m count=1")
                .waitFor();
        logger.info("generateResources: done");
    }

    @Test
    public void testApiVersion() throws Exception {
        ApiVersion apiVersion = client.getApiVersion();
        logger.info("apiVersion: " + apiVersion);
        assertThat(apiVersion.getBuild(), not(isEmptyOrNullString()));
        assertTrue("2.6.37".equalsIgnoreCase(apiVersion.getBuild()));
        assertTrue("v1".equalsIgnoreCase(apiVersion.getApiVersion()));
    }

    @Test
    public void testCapacity() throws Exception {
        DiskCapacity capacity = client.getCapacity();
        logger.info("capacity: " + capacity);
        assertThat(capacity.getTotalSpace(), greaterThan(0L));
        assertThat(capacity.getTrashSize(), greaterThanOrEqualTo(0L));
        assertThat(capacity.getUsedSpace(), greaterThanOrEqualTo(0L));
        assertThat(capacity.getSystemFolders(), isA(Map.class));
        assertThat(capacity.getSystemFolders(), hasKey("applications"));
        assertThat(capacity.getSystemFolders(), hasKey("downloads"));
    }

    @Test
    public void testListResources() throws Exception {
        int limit = 50;
        Resource resource = client.listResources(new ResourcesArgs.Builder()
                .setPath("/")
                .setLimit(limit)
                .setOffset(10)
                .build());
        assertTrue("dir".equals(resource.getType()));
        assertEquals(resource.getPath(), new ResourcePath("disk", "/"));
        logger.info("self: " + resource);

        ResourceList items = resource.getItems();
        assertFalse(items == null);
        for (Resource item : items.getItems()) {
            logger.info("item: " + item);
        }
        assertThat(items.getItems(), hasSize(limit));
        assertThat(items.getItems().get(0).getName(), not(isEmptyOrNullString()));
    }

    @Test
    public void testListResources2() throws Exception {
        int limit = 50;
        int offset = 0;
        ResourceList items;
        do {
            Resource resource = client.listResources(new ResourcesArgs.Builder()
                    .setPath("/")
                    .setLimit(limit)
                    .setOffset(offset)
                    .build());
            assertTrue("dir".equals(resource.getType()));
            assertEquals(resource.getPath(), new ResourcePath("disk", "/"));
            logger.info("self: " + resource);
            items = resource.getItems();
            assertFalse(items == null);
            for (Resource item : items.getItems()) {
                logger.info("item: " + item);
            }
            offset += limit;
            logger.info("offset: " + offset);
        } while(items.getItems().size() >= limit);
    }

    @Test
    public void testListResourcesHandler() throws Exception {
        ResourcesHandler parsingHandler = new ResourcesHandler() {
            final List<Resource> items = new ArrayList<>();

            @Override
            public void handleSelf(Resource item) {
                logger.info("self: " + item);
            }

            @Override
            public void handleItem(Resource item) {
                items.add(item);
                logger.info("item: " + item);
            }

            @Override
            public void onFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
            }
        };
        client.listResources(new ResourcesArgs.Builder()
                .setPath("/")
                .setParsingHandler(parsingHandler)
                .build());
    }

    @Test
    public void testFlatListResources() throws Exception {
        int limit = 50;
        ResourceList resourceList = client.flatListResources(new ResourcesArgs.Builder()
                .setMediaType("video")
                .setLimit(limit)
                .setOffset(10)
                .build());
        logger.info("resourceList: " + resourceList);
        assertEquals(resourceList.getPath(), null);

        List<Resource> items = resourceList.getItems();
        assertFalse(items == null);
        for (Resource item : items) {
            logger.info("item: " + item);
            assertTrue(item.getMimeType().contains("video"));
        }
        assertThat(items, hasSize(limit));
        assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
    }

    @Test
    public void testFlatListResourcesHandler() throws Exception {
        ResourcesHandler parsingHandler = new ResourcesHandler() {
            final List<Resource> items = new ArrayList<>();

            @Override
            public void handleSelf(Resource item) {
                throw new AssertionError();
            }

            @Override
            public void handleItem(Resource item) {
                items.add(item);
                logger.info("item: " + item);
            }

            @Override
            public void onFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
            }
        };
        client.flatListResources(new ResourcesArgs.Builder()
                .setMediaType("audio")
                .setParsingHandler(parsingHandler)
                .build());
    }

    @Test
    public void testUploadedListResources() throws Exception {
        int limit = 50;
        ResourceList resourceList = client.uploadedListResources(new ResourcesArgs.Builder()
                .setMediaType("video")
                .setLimit(limit)
                .setOffset(10)
                .build());
        logger.info("resourceList: " + resourceList);
        assertEquals(resourceList.getPath(), null);

        List<Resource> items = resourceList.getItems();
        assertFalse(items == null);
        for (Resource item : items) {
            logger.info("item: " + item);
            assertTrue(item.getMimeType().contains("video"));
        }
        assertThat(items, hasSize(limit));
        assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
    }

    @Test
    public void testUploadedListResourcesHandler() throws Exception {
        ResourcesHandler parsingHandler = new ResourcesHandler() {
            final List<Resource> items = new ArrayList<>();

            @Override
            public void handleSelf(Resource item) {
                throw new AssertionError();
            }

            @Override
            public void handleItem(Resource item) {
                items.add(item);
                logger.info("item: " + item);
            }

            @Override
            public void onFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
            }
        };
        client.uploadedListResources(new ResourcesArgs.Builder()
                .setMediaType("audio")
                .setParsingHandler(parsingHandler)
                .build());
    }

    @Test
    public void testPatchResource() throws Exception {
        Map<String,Object> fooBar = new LinkedHashMap<>();
        fooBar.put("foo", 1);
        fooBar.put("bar", 2);
        Map <String, Map<String,Object>> properties = new LinkedHashMap<>();
        properties.put("custom_properties", fooBar);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();
        TypedOutput body = new GsonConverter(gson).toBody(properties);

        Resource resource = client.patchResource(new ResourcesArgs.Builder()
                        .setPath("/0-test")
                        .setBody(body)
                        .build());
        assertTrue("dir".equals(resource.getType()));
        assertEquals(resource.getPath(), new ResourcePath("disk", "/0-test"));
        logger.info("self: " + resource);
    }

    @Test
    public void testListTrash() throws Exception {
        final List<Resource> items = new ArrayList<>();
        ResourcesHandler parsingHandler = new ResourcesHandler() {
            @Override
            public void handleItem(Resource item) {
                logger.info("item: " + item);
                items.add(item);
//                if (new ResourcePath("trash", "/.TheUnarchiverTemp0").equals(item.getPath())) {
//                    assertEquals(item.getOriginPath(), new ResourcePath("disk", "/apple/.TheUnarchiverTemp0"));
//                }
            }

            @Override
            public void onFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                if (itemsOnPage > 0) {
                    assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
                }
            }
        };
        client.listTrash(new ResourcesArgs.Builder()
                .setPath("/")
                .setParsingHandler(parsingHandler)
                .build());
    }

    private void checkResult(Link link)
            throws IOException, InterruptedException, WrongMethodException, HttpCodeException {
        switch (link.getHttpStatus()) {
            case done:
                break;
            case inProgress:
                Operation operation = client.waitProgress(link, new Runnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            if (++i > 100) {
                                throw new AssertionError();
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                assertThat(operation.getStatus(), not(isEmptyOrNullString()));
                assertTrue(operation.isSuccess());
                break;
            case error:
            default:
                throw new AssertionError();
        }
    }

    @Test
    public void testDropTrash() throws Exception {
        String name = "/drop-trash-test";
        String path = "/0-test"+name;

        try {
            client.delete(path, true);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        Link link = client.makeFolder(path);
        assertTrue(link.getHref() != null);
        assertTrue(link.getHref().equals(client.getUrl() + "/v1/disk/resources?path="
                + URLEncoder.encode("disk:" + path, "UTF-8")));

        String sub = path + "/sub1";
        Link link2 = client.makeFolder(sub);
        assertTrue(link2.getHref() != null);
        assertTrue(link2.getHref().equals(client.getUrl() + "/v1/disk/resources?path="
                + URLEncoder.encode("disk:" + sub, "UTF-8")));

        checkResult(client.delete(path, false));
        checkResult(client.dropTrash(name));
    }

    @Test
    public void testRestoreTrash() throws Exception {
        String name = "/restore-trash-test";
        String path = "/0-test"+name;

        try {
            client.delete(path, true);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        Link link = client.makeFolder(path);
        assertTrue(link.getHref() != null);
        assertTrue(link.getHref().equals(client.getUrl() + "/v1/disk/resources?path="
                + URLEncoder.encode("disk:" + path, "UTF-8")));

        checkResult(client.delete(path, false));
        checkResult(client.restoreTrash(name, null, null));
    }

    @Test
    public void testDownloadFile() throws Exception {
        String path = "/yac-qr.png";
        File local = new File("/tmp/"+path);
        assertFalse(local.exists());
        client.downloadFile(path, local, null, new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {
                logger.info("updateProgress: " + loaded + " / " + total);
            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });
        logger.info("length: " + local.length());
        assertTrue(local.length() == 709L);
        assertTrue(local.delete());
    }

    @Test
    public void testHash() throws Exception {
        File file = new File("testResources/test-upload-001.bin");
        Hash hash = Hash.getHash(file);
        assertTrue(hash.getSize() == file.length());
        assertTrue("11968e619814b8f7f0367241d6ee1c2d".equalsIgnoreCase(hash.getMd5()));
        assertTrue("18339f4b55f3771b5486595686d0d43ff63da17edd0b30edb7e95f69abce5fad".equalsIgnoreCase(hash.getSha256()));
    }

    @Test
    public void testSaveFromUrl() throws Exception {
        String url = "http://yastatic.net/morda-logo/i/apple-touch-icon/ru-76x76.png";
        String path = "/0-test/save-from-url-test.png";

        try {
            client.delete(path, false);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }

        Link saveLink = client.saveFromUrl(url, path, null);
        Operation operation = client.getOperation(saveLink);
        logger.info("operation: "+operation);
        assertThat(operation.getStatus(), not(isEmptyOrNullString()));
    }

    @Test(expected = ServerIOException.class)   // TODO change the exception
    public void testUploadFileOverwriteFailed() throws Exception {
        String path = "/yac-qr.png";
        client.getUploadLink(path, false, null);
    }

    @Test
    public void testUploadFileResume() throws Exception {
        String name = "test-upload-002.bin";
        String serverPath = "/0-test/" + name;
//        String serverPath = "/0-test/" + UUID.randomUUID().toString();
        File local = new File("testResources/" + name);
        assertTrue(local.exists());
        assertTrue(local.length() == 1048576);

        Link link = client.getUploadLink(serverPath, true, null);

        final int lastPass = 2;
        for (int i = 0; i <= lastPass; i++) {
            final int pass = i;
            try {
                client.uploadFile(link, true, local, null, new ProgressListener() {
                    boolean doCancel = false;

                    @Override
                    public void updateProgress(long loaded, long total) {
                        logger.info("updateProgress: pass=" + pass + ": " + loaded + " / " + total);
                        if (pass == 0 && loaded >= 10240) {
                            doCancel = true;
                        }
                        if (pass == 1 && loaded >= 102400) {
                            doCancel = true;
                        }
                    }

                    @Override
                    public boolean hasCancelled() {
                        if (doCancel) {
                            logger.info("cancelled");
                        }
                        return doCancel;
                    }
                });
            } catch (CancelledUploadingException ex) {
                logger.info("CancelledUploadingException");
            } catch (IOException ex) {
                if (pass >= lastPass) {
                    throw ex;
                }
            }
        }
    }

    @Test
    public void testMakeFolder() throws Exception {
        String path = "/0-test/make-folder-test";

        try {
            client.delete(path, false);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }

        Link link = client.makeFolder(path);
        logger.info("link: "+link);
        Operation operation = client.getOperation(link);
        logger.info("operation: "+operation);
        assertTrue(operation.getStatus() == null);
    }

    @Test
    public void testCopyFolder() throws Exception {
        String from = "/0-test/copy-folder-test-src";
        String to = "/0-test/copy-folder-test-dst";

        try {
            client.makeFolder(from);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        try {
            client.delete(to, false);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }

        Link link = client.copy(from, to, false);
        logger.info("link: "+link);
        Operation operation = client.getOperation(link);
        logger.info("operation: "+operation);
        assertTrue(operation.getStatus() == null);
    }

    @Test
    public void testMoveFolder() throws Exception {
        String from = "/0-test/move-folder-test-src";
        String to = "/0-test/move-folder-test-dst";

        try {
            client.makeFolder(from);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        try {
            client.delete(to, false);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }

        Link link = client.move(from, to, false);
        logger.info("link: "+link);
        Operation operation = client.getOperation(link);
        logger.info("operation: "+operation);
        assertTrue(operation.getStatus() == null);
    }

    @Test
    public void testPublishAndUnpublish() throws Exception {
        String path = "/0-test/publish-test";

        try {
            client.delete(path, false);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        client.makeFolder(path);

        client.publish(path);
        client.listResources(new ResourcesArgs.Builder()
                .setPath(path)
                .setParsingHandler(new ResourcesHandler() {
                    @Override
                    public void handleItem(Resource item) {
                        assertThat(item.getPublicKey(), not(isEmptyOrNullString()));
                        assertThat(item.getPublicUrl(), not(isEmptyOrNullString()));
                    }
                })
                .build());

        client.unpublish(path);
        client.listResources(new ResourcesArgs.Builder()
                .setPath(path)
                .setParsingHandler(new ResourcesHandler() {
                    @Override
                    public void handleItem(Resource item) {
                        assertThat(item.getPublicKey(), isEmptyOrNullString());
                        assertThat(item.getPublicUrl(), isEmptyOrNullString());
                    }
                })
                .build());
    }

    @Test
    public void testListPublicResources() throws Exception {
        String path = "/0-test/list-public-resources-test";

        try {
            client.delete(path, true);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        try {
            client.makeFolder(path);
        } catch (ServerIOException ex) {
            ex.printStackTrace();
        }
        for (int i = 1; i < 6; i++) {
            try {
                client.makeFolder(path + "/folder-" + i);
            } catch (ServerIOException ex) {
                ex.printStackTrace();
            }
        }

        Link link = client.publish(path);
        logger.info("link: "+link);

        try {
            final String[] publicKey = new String[1];
            client.listResources(new ResourcesArgs.Builder()
                    .setPath(path)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleSelf(Resource item) {
                            publicKey[0] = item.getPublicKey();
                        }
                    })
                    .build());
            logger.info("publicKey: " + publicKey[0]);

            client.listPublicResources(new ResourcesArgs.Builder()
                    .setPublicKey(publicKey[0])
                    .setParsingHandler(new ResourcesHandler() {
                        final List<Resource> items = new ArrayList<>();

                        @Override
                        public void handleItem(Resource item) {
                            items.add(item);
                            logger.info("item: " + item);
                        }

                        @Override
                        public void onFinished(int itemsOnPage) {
                            assertThat(items, hasSize(itemsOnPage));
                            assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
                        }
                    })
                    .build());

        } finally {
            try {
                client.unpublish(path);
            } catch (ServerIOException ex) {
                ex.printStackTrace();
            }
            client.delete(path, true);
        }
    }

    @Test
    public void testDownloadAndSavePublicResource() throws Exception {
        String path = "/yac-qr.png";
        File local = new File("/tmp/" + path);
        assertFalse(local.exists());

        Link link = client.publish(path);
        logger.info("link: "+link);
        try {
            final String[] publicKey = new String[1];
            client.listResources(new ResourcesArgs.Builder()
                    .setPath(path)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleSelf(Resource item) {
                            publicKey[0] = item.getPublicKey();
                        }
                    })
                    .build());

            Link savedLink = client.savePublicResource(publicKey[0], null, null);
            logger.info("savedLink: "+savedLink);
            // TODO check saved file

            client.downloadPublicResource(publicKey[0], "", local, null, new ProgressListener() {
                @Override
                public void updateProgress(long loaded, long total) {
                    logger.info("updateProgress: " + loaded + " / " + total);
                }

                @Override
                public boolean hasCancelled() {
                    return false;
                }
            });
            logger.info("length: " + local.length());
            assertTrue(local.length() == 709L);
            assertTrue(local.delete());

        } finally {
            client.unpublish(path);
        }
    }

    private static class CredentialsImpl implements Credentials {

        private final String user, token;

        private CredentialsImpl(String user, String token) {
            this.user = user;
            this.token = token;
        }

        @Override
        public String getUser() {
            return user;
        }

        @Override
        public String getToken() {
            return token;
        }
    }
}
