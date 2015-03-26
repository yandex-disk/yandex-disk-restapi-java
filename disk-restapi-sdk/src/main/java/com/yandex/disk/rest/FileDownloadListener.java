/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileDownloadListener extends DownloadListener {

    private final File saveTo;
    private final ProgressListener progressListener;

    public FileDownloadListener(File saveTo, ProgressListener progressListener) {
        this.saveTo = saveTo;
        this.progressListener = progressListener;
    }

    @Override
    public OutputStream getOutputStream(boolean append)
            throws FileNotFoundException {
        return new FileOutputStream(saveTo, append);
    }

    @Override
    public long getLocalLength() {
        return saveTo.length();
    }

    @Override
    public void updateProgress(long loaded, long total) {
        if (progressListener != null) {
            progressListener.updateProgress(loaded, total);
        }
    }

    @Override
    public boolean hasCancelled() {
        return progressListener != null && progressListener.hasCancelled();
    }
}
