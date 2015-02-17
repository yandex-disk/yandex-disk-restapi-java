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
import com.yandex.disk.rest.TransportClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

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

    private static final int ITEMS_PER_REQUEST = 100;

    private static Collator collator = Collator.getInstance();
    static {
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    }
    private final Comparator<ListItem> FILE_ITEM_COMPARATOR = new Comparator<ListItem>() {
        @Override
        public int compare(ListItem f1, ListItem f2) {
            if (f1.isCollection() && !f2.isCollection()) {
                return -1;
            } else if (f2.isCollection() && !f1.isCollection()) {
                return 1;
            } else {
                return collator.compare(f1.getDisplayName(), f2.getDisplayName());
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
        TransportClient client = null;
        try {
            client = TransportClient.getInstance(credentials);
            // for() {
            client.listResources(new ResourcesArgs.Builder()
                    .setPath(dir)
                    .setLimit(ITEMS_PER_REQUEST)
//                    .setOffset(0)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleItem(Resource item) {
                            fileItemList.add(ListItemUtils.convert(item));
                        }

                        @Override
                        public void onFinished(int itemsOnPage) {
                            handler.post(new Runnable() {
                                @Override
                                public void run () {
                                    Collections.sort(fileItemList, FILE_ITEM_COMPARATOR);
                                    deliverResult(new ArrayList<>(fileItemList));
                                }
                            });
                        }
                    })
                    .build());
            // } for
            Collections.sort(fileItemList, FILE_ITEM_COMPARATOR);
            return fileItemList;
        } catch (ServerException ex) {
            Log.d(TAG, "loadInBackground", ex);
            exception = ex;
        } catch (IOException ex) {
            Log.d(TAG, "loadInBackground", ex);
            exception = ex;
        } finally {
            TransportClient.shutdown(client);
        }
        return fileItemList;
    }

    public Exception getException() {
        return exception;
    }
}
