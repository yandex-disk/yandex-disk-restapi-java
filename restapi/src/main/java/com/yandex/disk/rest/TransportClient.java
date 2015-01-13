package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.UnknownServerWebdavException;
import com.yandex.disk.rest.exceptions.WebdavClientInitException;
import com.yandex.disk.rest.exceptions.WebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.Capacity;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;
import com.yandex.disk.rest.retrofit.CloudApi;
import com.yandex.disk.rest.retrofit.ErrorHandlerImpl;
import com.yandex.disk.rest.retrofit.RequestInterceptorImpl;
import com.yandex.disk.rest.util.Hash;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RestAdapter;

public class TransportClient {

    private static final String TAG = "TransportClient";

    private static final RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.FULL;

    private static final int NETWORK_TIMEOUT = 30000;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String USER_AGENT = "Cloud API Android Client Example/1.0";

    private static URL serverURL;

    static {
        try {
            serverURL = new URL("https://cloud-api.yandex.net:443");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final List<CustomHeader> commonHeaders;
    private final HttpClient client;

    public TransportClient(final Credentials credentials, final int networkTimeout)
            throws WebdavClientInitException {
        this.commonHeaders = fillCommonHeaders(credentials.getToken());
        this.client = new HttpClient();
    }

    private static List<CustomHeader> fillCommonHeaders(final String token) {
        List<CustomHeader> list = new ArrayList<>();
        list.add(new CustomHeader(USER_AGENT_HEADER, USER_AGENT));
        list.add(new CustomHeader(AUTHORIZATION_HEADER, "OAuth " + token));
        return Collections.unmodifiableList(list);
    }

    private List<CustomHeader> getAllHeaders(final List<CustomHeader> headerList) {
        if (headerList == null) {
            return commonHeaders;
        }
        List<CustomHeader> list = new ArrayList<>(commonHeaders);
        list.addAll(headerList);
        return Collections.unmodifiableList(list);
    }

    public static TransportClient getInstance(final Credentials credentials)
            throws WebdavClientInitException {
        return new TransportClient(credentials, NETWORK_TIMEOUT);
    }

    private String getUrl() {
        return serverURL.toExternalForm();
    }

    private RestAdapter.Builder getRestAdapterBuilder(final List<CustomHeader> headerList) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(getUrl())
                .setRequestInterceptor(new RequestInterceptorImpl(commonHeaders, headerList))
                .setErrorHandler(new ErrorHandlerImpl())
                .setLogLevel(LOG_LEVEL);
    }

    private RestAdapter.Builder getRestAdapterBuilder() {
        return getRestAdapterBuilder(null);
    }

    public ApiVersion getApiVersion()
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getApiVersion();
    }

    public Operation getOperation(final String operationId)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getOperation(operationId);
    }

    public Capacity getCapacity()
            throws IOException, WebdavIOException {
        return getCapacity(null);
    }

    public Capacity getCapacity(final String fields)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getCapacity(fields);
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

    public void downloadFile(final String path, final File saveTo, final List<CustomHeader> headerList,
                             final ProgressListener progressListener)
            throws IOException, WebdavException {
        Link link = getRestAdapterBuilder(headerList).build()
                .create(CloudApi.class)
                .getDownloadLink(path);
        Log.d(TAG, "getDownloadLink(): " + link);

        new HttpClientIO(client, getAllHeaders(headerList))
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public Link getUploadLink(final String serverPath, final boolean overwrite, final List<CustomHeader> headerList)
            throws WebdavIOException, UnknownServerWebdavException {
        Link link = getRestAdapterBuilder(headerList).build()
                .create(CloudApi.class)
                .getUploadLink(serverPath, overwrite);
        Log.d(TAG, "getLink(): " + link);

        if (!"PUT".equalsIgnoreCase(link.getMethod())) {
            throw new UnknownServerWebdavException("Method in Link object is not PUT"); // TODO throw a proper exception
        }

        return link;
    }

    public void uploadFile(final Link link, final boolean resumeUpload, final File localSource,
                           final List<CustomHeader> headerList, final ProgressListener progressListener)
            throws IOException, WebdavException {
        HttpClientIO clientIO = new HttpClientIO(client, getAllHeaders(headerList));
        long startOffset = 0;
        if (resumeUpload) {
            Hash hash = Hash.getHash(localSource);
            startOffset = clientIO.headUrl(link.getHref(), hash);
            Log.d("head: startOffset="+startOffset);
        }
        clientIO.uploadFile(link.getHref(), localSource, startOffset, progressListener);
    }

}