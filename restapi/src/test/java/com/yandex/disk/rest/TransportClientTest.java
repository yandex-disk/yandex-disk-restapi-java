package com.yandex.disk.rest;

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
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnit4.class)
public class TransportClientTest {

    private TransportClient client;

    @Before
    public void setUp() throws Exception {
        System.out.println("pwd: " + new File(".").getAbsolutePath());

        FileInputStream propertiesFile = new FileInputStream("local.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile);

        String user = properties.getProperty("test.user");
        String token = properties.getProperty("test.token");

        assertThat(user, notNullValue());
        assertThat(token, notNullValue());

        Credentials credentials = new Credentials(user, token);

        client = TransportClient.getInstance(credentials);
    }

    @Ignore
    @Test
    public void testOperation() throws Exception {
        Operation operation = client.getOperation("5");
        System.out.println("operation: "+operation);
        assertThat(operation.getStatus(), not(isEmptyOrNullString()));
    }

    @Ignore
    @Test
    public void testDiskMeta() throws Exception {
        DiskMeta meta = client.getDiskMeta();
        System.out.println("meta: " + meta);
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
                System.out.println("item: " + item);
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
//        System.out.println("item to delete: " + item);
//
//        Link link = client.dropTrash(item.getFullPath(), null);
//        System.out.println("dropTrash result: " + link);

    }

//    @Ignore
    @Test
    public void testDownloadFile() throws Exception {
        String path = "/yac-qr.png";
        File local = new File("/tmp/"+path);
        client.downloadFile(path, null, local, new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {
                System.out.println("updateProgress: " + loaded + " / " + total);
            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });
        assertThat(local.length(), not(lessThan(709L)));
    }
}
