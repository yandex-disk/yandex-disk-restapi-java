package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.WebdavClientInitException;
import com.yandex.disk.rest.exceptions.WebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.DiskMeta;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;
import com.yandex.disk.rest.retrofit.CloudApi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class TransportClient {

    private static final String TAG = "TransportClient";

    private static final RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.FULL;

    private static final int NETWORK_TIMEOUT = 30000;

    private static final String USER_AGENT = "Cloud API Android Client Example/1.0";
    private static final String ATTR_ETAG_FROM_REDIRECT = "yandex.etag-from-redirect";

    private static URL serverURL;

    static {
        try {
            serverURL = new URL("https://cloud-api.yandex.net:443");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final List<CustomHeader> commonHeaders;
    private final MyOkClient client;

    public TransportClient(final Credentials credentials, final int networkTimeout)
            throws WebdavClientInitException {
        this.commonHeaders = fillCommonHeaders(credentials.getToken());
        this.client = new MyOkClient();
    }

    private static List<CustomHeader> fillCommonHeaders(final String token) {
        List<CustomHeader> list = new ArrayList<>();
        list.add(new CustomHeader("User-Agent", USER_AGENT));
        list.add(new CustomHeader("Authorization", "OAuth " + token));
        return list;
    }

    public static TransportClient getInstance(final Credentials credentials)
            throws WebdavClientInitException {
        return new TransportClient(credentials, NETWORK_TIMEOUT);
    }

    private String getUrl() {
        return serverURL.toExternalForm();
    }

    private RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            for (CustomHeader header : commonHeaders) {
                request.addHeader(header.getName(), header.getValue());
            }
        }
    };

    private RestAdapter.Builder getRestAdapterBuilder() {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(new ErrorHandlerImpl())
                .setLogLevel(LOG_LEVEL);
    }

    public Operation getOperation(final String operationId)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getOperation(operationId);
    }

    public DiskMeta getDiskMeta()
            throws IOException, WebdavIOException {
        return getDiskMeta(null);
    }

    public DiskMeta getDiskMeta(final String fields)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getDiskMeta(fields);
    }

    public void listResources(final String path, final ListParsingHandler handler)
            throws IOException, WebdavIOException {
        listResources(path, 0, 0, null, handler);
    }

    // TODO make test with limit, offset and sort
    public void listResources(final String path, final int limit, final int offset, final String sort, final ListParsingHandler handler)
            throws IOException, WebdavIOException {
        Resource resource = getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .listResources(path, limit, offset, sort);
        parseListResponse(resource, handler);
    }

    public void listTrash(final String path, final ListParsingHandler handler)
            throws IOException, WebdavIOException {
        listTrash(path, null, 0, 0, null, null, handler);
    }

    public void listTrash(final String path, final String fields, final int limit, final int offset,
                          final String sort, final String previewSize, final ListParsingHandler handler)
            throws IOException, WebdavIOException {
        Resource resource = getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .listTrash(path, fields, limit, offset, sort, previewSize);
        parseListResponse(resource, handler);
    }

    public Link dropTrash(final String path, final String fields)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .dropTrash(path, fields);
    }

    private void parseListResponse(final Resource resource, final ListParsingHandler handler) {
        ResourceList items = resource.getItems();
        int size = items.getItems().size();
        Log.d(TAG, "parseListResponse: size=" + size);
        for (Resource item : items.getItems()) {
//            if (handler.hasCancelled()) { TODO
//                return;
//            }
            handler.handleItem(item);
        }
        handler.onPageFinished(size);
    }

    public void downloadFile(final String path, final List<CustomHeader> headerList, final File saveTo, final ProgressListener progressListener)
            throws IOException, WebdavException {
        Link link = getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getDownloadLink(path);
        Log.d(TAG, "getDownloadLink(): " + link);

        new Download(client.getClient(), commonHeaders)
                .downloadUrl(link.getHref(), headerList, new FileDownloadListener(saveTo, progressListener));
    }
}