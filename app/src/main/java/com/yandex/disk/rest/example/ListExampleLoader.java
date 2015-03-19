/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.example;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.ResourcesHandler;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListExampleLoader extends AsyncTaskLoader<List<ListItem>> {

    private static String TAG = "ListExampleLoader";

    private Credentials credentials;
    private String dir;
    private Handler handler;

    private List<ListItem> fileItemList;
    private Exception exception;
    private boolean hasCancelled;

    private static final int ITEMS_PER_REQUEST = 20;

    private static Collator collator = Collator.getInstance();
    static {
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    }
    private final Comparator<ListItem> FILE_ITEM_COMPARATOR = new Comparator<ListItem>() {
        @Override
        public int compare(ListItem f1, ListItem f2) {
            if (f1.isDir() && !f2.isDir()) {
                return -1;
            } else if (f2.isDir() && !f1.isDir()) {
                return 1;
            } else {
                return collator.compare(f1.getName(), f2.getName());
            }
        }
    };

    public ListExampleLoader(Context context, Credentials credentials, String dir) {
        super(context);
        handler = new Handler();
        this.credentials = credentials;
        this.dir = dir;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        hasCancelled = true;
    }

    @Override
    public List<ListItem> loadInBackground() {
        fileItemList = new ArrayList<>();
        hasCancelled = false;
        int offset = 0;
        RestClient client = null;
        try {
            client = RestClientUtil.getInstance(credentials);
            int size;
            do {
                Resource resource = client.listResources(new ResourcesArgs.Builder()
                        .setPath(dir)
                        .setSort(ResourcesArgs.Sort.name)
                        .setLimit(ITEMS_PER_REQUEST)
                        .setOffset(offset)
                        .setParsingHandler(new ResourcesHandler() {
                            @Override
                            public void handleItem(Resource item) {
                                fileItemList.add(new ListItem(item));
                            }
                        })
                        .build());
                offset += ITEMS_PER_REQUEST;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(fileItemList, FILE_ITEM_COMPARATOR);
                        deliverResult(new ArrayList<>(fileItemList));
                    }
                });
                size = resource.getItems().getItems().size();
            } while (!hasCancelled && size >= ITEMS_PER_REQUEST);
            return fileItemList;
        } catch (IOException | ServerException ex) {
            Log.d(TAG, "loadInBackground", ex);
            exception = ex;
        } finally {
            RestClient.shutdown(client);
        }
        return fileItemList;
    }

    public Exception getException() {
        return exception;
    }
}
