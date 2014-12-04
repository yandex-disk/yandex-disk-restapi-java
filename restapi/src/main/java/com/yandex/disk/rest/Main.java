package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.WebdavClientInitException;
import com.yandex.disk.rest.exceptions.WebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

    final static Credentials credentials = new Credentials("olegnet", "b8cebc99da0047f193082d30ec198449");

    public static void main(String[] args) {
        try {
            list("/");

            downloadFile("/yac-qr.png");

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static void list(String dir)
            throws WebdavClientInitException, IOException, WebdavIOException {
        TransportClientOkhttp client = TransportClientOkhttp.getInstance(credentials);
        client.getList(dir, 0, new ListParsingHandler() {
            @Override
            public boolean handleItem(ListItem item) {
                System.out.println(item);
                return true;
            }
        });
    }

    private static void downloadFile(String path)
            throws WebdavException, IOException {
        TransportClientOkhttp client = TransportClientOkhttp.getInstance(credentials);
        client.downloadFile(path, new File("/tmp/"+path), new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {
                System.out.println("updateProgress: "+loaded+" / "+total);
            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });
    }
}
