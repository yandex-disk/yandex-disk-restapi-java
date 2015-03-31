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
