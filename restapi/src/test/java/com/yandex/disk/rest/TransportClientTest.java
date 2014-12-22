package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.CancelledUploadingException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.DiskMeta;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TransportClientTest {

    private TransportClient client;

    @Before
    public void setUp() throws Exception {
        Log.d("pwd: " + new File(".").getAbsolutePath());

        FileInputStream propertiesFile = new FileInputStream("local.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);

        String user = properties.getProperty("test.user");
        String token = properties.getProperty("test.token");

        assertThat(user, notNullValue());
        assertThat(token, notNullValue());

        Credentials credentials = new Credentials(user, token);

        client = TransportClient.getInstance(credentials);

        generateResources();
    }

    private void generateResources() throws Exception {
        // TODO move to proper directory
        Runtime.getRuntime().exec("/usr/bin/env dd if=/dev/urandom of=testResources/test-upload-002.bin bs=1m count=1")
                .waitFor();
        Log.d("generateResources: done");
    }

    @Ignore
    @Test
    public void testOperation() throws Exception {
        Operation operation = client.getOperation("5");
        Log.d("operation: "+operation);
        assertThat(operation.getStatus(), not(isEmptyOrNullString()));
    }

    @Ignore
    @Test
    public void testDiskMeta() throws Exception {
        DiskMeta meta = client.getDiskMeta();
        Log.d("meta: " + meta);
        assertThat(meta.getTotalSpace(), greaterThan(0L));
        assertThat(meta.getTrashSize(), greaterThanOrEqualTo(0L));
        assertThat(meta.getUsedSpace(), greaterThanOrEqualTo(0L));
        assertThat(meta.getSystemFolders(), isA(Map.class));
        assertThat(meta.getSystemFolders(), hasKey("applications"));
        assertThat(meta.getSystemFolders(), hasKey("downloads"));
    }

    @Ignore
    @Test
    public void testListResources() throws Exception {
        final List<Resource> items = new ArrayList<>();
        client.listResources("/", new ListParsingHandler() {
            @Override
            public boolean handleItem(Resource item) {
                items.add(item);
                Log.d("item: " + item);
                return true;
            }

            @Override
            public void onPageFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
            }
        });
    }

    @Ignore
    @Test
    public void testTrash() throws Exception {
        final List<Resource> items = new ArrayList<>();
        client.listTrash("/", new ListParsingHandler() {
            @Override
            public boolean handleItem(Resource item) {
                items.add(item);
                return true;
            }

            @Override
            public void onPageFinished(int itemsOnPage) {
                assertThat(items, hasSize(itemsOnPage));
                assertThat(items.get(0).getName(), not(isEmptyOrNullString()));
            }
        });

//        ListItem item = items.get(0);
//        Log.d("item to delete: " + item);
//
//        Link link = client.dropTrash(item.getFullPath(), null);
//        Log.d("dropTrash result: " + link);

    }

    @Ignore
    @Test
    public void testDownloadFile() throws Exception {
        String path = "/yac-qr.png";
        File local = new File("/tmp/"+path);
        assertFalse(local.exists());
        client.downloadFile(path, local, null, new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {
                Log.d("updateProgress: " + loaded + " / " + total);
            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });
        Log.d("length: " + local.length());
        assertTrue(local.length() == 709L);
        assertTrue(local.delete());
    }

    @Ignore
    @Test
    public void testHash() throws Exception {
        File file = new File("testResources/test-upload-001.bin");
        Hash hash = Hash.getHash(file);
        assertTrue(hash.getSize() == file.length());
        assertTrue("11968e619814b8f7f0367241d6ee1c2d".equalsIgnoreCase(hash.getMd5()));
        assertTrue("18339f4b55f3771b5486595686d0d43ff63da17edd0b30edb7e95f69abce5fad".equalsIgnoreCase(hash.getSha256()));
    }

    @Ignore
    @Test(expected = WebdavIOException.class)   // TODO change the exception
    public void testUploadFileOverwriteFailed() throws Exception {
        String path = "/yac-qr.png";
        client.getUploadLink(path, false, null);
    }

//    @Ignore
    @Test
    public void testUploadFileResume() throws Exception {
        String name = "test-upload-002.bin";
        String serverPath = "/0-test/" + name;
//        String serverPath = "/0-test/" + UUID.randomUUID().toString();
        File local = new File("testResources/" + name);
        assertTrue(local.exists());
        assertTrue(local.length() == 1048576);

        Link link = client.getUploadLink(serverPath, true, null);

//        final int lastPass = 2;
        final int lastPass = 1;
        for (int i = 0; i <= lastPass; i++) {
            final int pass = i;
            try {
//                Link link = client.getUploadLink(serverPath, true);
//                client.uploadFile(link, true, local, null, null);
                client.uploadFile(link, true, local, null, new ProgressListener() {
                    boolean doCancel = false;

                    @Override
                    public void updateProgress(long loaded, long total) {
                        Log.d("updateProgress: pass=" + pass + ": " + loaded + " / " + total);
                        if (pass == 0 && loaded >= 10240) {
                            doCancel = true;
                        }
//                        if (pass == 1 && loaded >= 102400) {
//                            doCancel = true;
//                        }
                    }

                    @Override
                    public boolean hasCancelled() {
                        if (doCancel) {
                            Log.d("###### cancelled");
                        }
                        return doCancel;
                    }
                });
            } catch (CancelledUploadingException ex) {
                Log.d("CancelledUploadingException");
            } catch (IOException ex) {
                if (pass >= lastPass) {
                    throw ex;
                }
            }
        }
    }
}
