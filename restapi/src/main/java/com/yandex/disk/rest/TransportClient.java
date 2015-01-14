package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.ServerClientInitException;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.UnknownServerException;
import com.yandex.disk.rest.exceptions.WrongMethodException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskCapacity;
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
            throws ServerClientInitException {
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
            throws ServerClientInitException {
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

    private CloudApi call(final List<CustomHeader> headerList) {
        return getRestAdapterBuilder(headerList).build()
                .create(CloudApi.class);
    }

    private CloudApi call() {
        return getRestAdapterBuilder(null).build()
                .create(CloudApi.class);
    }

    public ApiVersion getApiVersion()
            throws IOException, ServerIOException {
        return call().getApiVersion();
    }

    public Operation getOperation(final String operationId)
            throws IOException, ServerIOException {
        return call().getOperation(operationId);
    }

    public Operation getOperation(final Link link)
            throws IOException, UnknownServerException, WrongMethodException {
        if (!"GET".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not GET");
        }
        Operation operation = new HttpClientIO(client, getAllHeaders(null))
                .getJson(link.getHref(), Operation.class);
        Log.d(TAG, "getOperation: " + operation);
        return operation;
    }

    public DiskCapacity getCapacity()
            throws IOException, ServerIOException {
        return getCapacity(null);
    }

    public DiskCapacity getCapacity(final String fields)
            throws IOException, ServerIOException {
        return call().getCapacity(fields);
    }

    public void listResources(final String path, final ListParsingHandler handler)
            throws IOException, ServerIOException {
        listResources(path, null, 0, 0, null, null, handler);
    }

    // TODO make test with fields, limit, offset, sort and previewSize
    public void listResources(final String path, final String fields, final int limit, final int offset,
                              final String sort, final String previewSize, final ListParsingHandler handler)
            throws IOException, ServerIOException {
        Resource resource = call().listResources(path, fields, limit, offset, sort, previewSize);
        parseListResponse(resource, handler);
    }

    public void listTrash(final String path, final ListParsingHandler handler)
            throws IOException, ServerIOException {
        listTrash(path, null, 0, 0, null, null, handler);
    }

    public void listTrash(final String path, final String fields, final int limit, final int offset,
                          final String sort, final String previewSize, final ListParsingHandler handler)
            throws IOException, ServerIOException {
        Resource resource = call().listTrash(path, fields, limit, offset, sort, previewSize);
        parseListResponse(resource, handler);
    }

    public Link dropTrash(final String path)
            throws IOException, ServerIOException {
        return call().dropTrash(path);
    }

    private void parseListResponse(final Resource resource, final ListParsingHandler handler) {
        ResourceList items = resource.getItems();
        int size = items.getItems().size();
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
            throws IOException, ServerException {
        Link link = call(headerList).getDownloadLink(path);
        new HttpClientIO(client, getAllHeaders(headerList))
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public Link saveFromUrl(final String url, final String serverPath, final List<CustomHeader> headerList)
            throws ServerIOException, UnknownServerException {
        return call(headerList).saveFromUrl(url, serverPath);
    }

    public Link getUploadLink(final String serverPath, final boolean overwrite, final List<CustomHeader> headerList)
            throws ServerIOException, WrongMethodException {
        Link link = call(headerList).getUploadLink(serverPath, overwrite);
        if (!"PUT".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not PUT");
        }
        return link;
    }

    public void uploadFile(final Link link, final boolean resumeUpload, final File localSource,
                           final List<CustomHeader> headerList, final ProgressListener progressListener)
            throws IOException, ServerException {
        HttpClientIO clientIO = new HttpClientIO(client, getAllHeaders(headerList));
        long startOffset = 0;
        if (resumeUpload) {
            Hash hash = Hash.getHash(localSource);
            startOffset = clientIO.headUrl(link.getHref(), hash);
            Log.d("head: startOffset="+startOffset);
        }
        clientIO.uploadFile(link.getHref(), localSource, startOffset, progressListener);
    }

    // TODO catch 202 vs 204 http code from server
    public Link delete(final String path, final boolean permanently)
            throws ServerIOException {
        return call().delete(path, permanently);
    }

    public Link makeFolder(final String path)
            throws ServerIOException {
        return call().makeFolder(path);
    }

    public Link copy(final String from, final String path, final boolean overwrite)
            throws ServerIOException {
        return call().copy(from, path, overwrite);
    }

    public Link move(final String from, final String path, final boolean overwrite)
            throws ServerIOException, UnknownServerException {
        return call().move(from, path, overwrite);
    }

    public Link publish(final String path)
            throws ServerIOException {
        return call().publish(path);
    }

    public Link unpublish(final String path)
            throws ServerIOException {
        return call().unpublish(path);
    }
}