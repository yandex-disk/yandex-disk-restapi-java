package com.yandex.disk.rest;

import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.WrongMethodException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

public class TransportClient {

    private static final Logger logger = LoggerFactory.getLogger(TransportClient.class);

    private static final RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.FULL;

    private static URL serverURL;
    static {
        try {
            serverURL = new URL("https://cloud-api.yandex.net");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final List<CustomHeader> commonHeaders;
    private final HttpClient client;

    public TransportClient(final Credentials credentials) {
        this(credentials, new HttpClient());
    }

    public TransportClient(Credentials credentials, HttpClient client) {
        this.commonHeaders = credentials.getHeaders();
        this.client = client;
    }

    public void shutdown() {
//        client.getClient().cancel()
        // TODO nothing yet
    }

    public static void shutdown(TransportClient client) {
        client.shutdown();
    }

    private List<CustomHeader> getAllHeaders(final List<CustomHeader> headerList) {
        if (headerList == null) {
            return commonHeaders;
        }
        List<CustomHeader> list = new ArrayList<>(commonHeaders);
        list.addAll(headerList);
        return Collections.unmodifiableList(list);
    }

    /* package */ String getUrl() {
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
            throws IOException, WrongMethodException, HttpCodeException {
        if (!"GET".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not GET");
        }
        Operation operation = new HttpClientIO(client, getAllHeaders(null))
                .getOperation(link.getHref());
        logger.debug("getOperation: " + operation);
        return operation;
    }

    public Operation waitProgress(final Link link, final Runnable waiting)
            throws IOException, WrongMethodException, HttpCodeException {
        while (true) {
            Operation operation = getOperation(link);
            if (!operation.isInProgress()) {
                return operation;
            }
            waiting.run();
        }
    }

    public DiskCapacity getCapacity()
            throws IOException, ServerIOException {
        return getCapacity(null);
    }

    public DiskCapacity getCapacity(final String fields)
            throws IOException, ServerIOException {
        return call().getCapacity(fields);
    }

    public Resource listResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = call().listResources(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public ResourceList flatListResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = call().flatListResources(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    public ResourceList uploadedListResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = call().uploadedListResources(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    public Resource patchResource(final ResourcesArgs args)
            throws ServerIOException {
        final Resource resource = call().patchResource(args.getPath(), args.getFields(), args.getBody());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public Resource listPublicResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = call().listPublicResources(args.getPublicKey(), args.getPath(),
                args.getFields(), args.getLimit(), args.getOffset(), args.getSort(),
                args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public Resource listTrash(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = call().listTrash(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public void dropTrash(final String path, final Callback<Link> callback)
            throws IOException, ServerIOException {
        call().dropTrash(path, callback);
    }

    public Link dropTrash(final String path)
            throws IOException, ServerIOException, URISyntaxException {
        return new HttpClientIO(client, getAllHeaders(null))
                .delete(new QueryBuilder(getUrl() + "/v1/disk/trash/resources")
                        .add("path", path)
                        .build());
    }

    public void restoreTrash(final String path, final String name, final Boolean overwrite,
                             final Callback<Link> callback)
            throws IOException, ServerIOException {
        call().restoreTrash(path, name, overwrite, callback);
    }

    public Link restoreTrash(final String path, final String name, final Boolean overwrite)
            throws IOException, ServerIOException {
        return new HttpClientIO(client, getAllHeaders(null))
                .put(new QueryBuilder(getUrl() + "/v1/disk/trash/resources/restore")
                        .add("path", path)
                        .add("name", name)
                        .add("overwrite", overwrite)
                        .build());
    }

    private void parseListResponse(final Resource resource, final ResourcesHandler handler) {
        handler.handleSelf(resource);
        ResourceList items = resource.getItems();
        int size = 0;
        if (items != null) {
            size = items.getItems().size();
            for (Resource item : items.getItems()) {
                handler.handleItem(item);
            }
        }
        handler.onFinished(size);
    }

    private void parseListResponse(final ResourceList resourceList, final ResourcesHandler handler) {
        List<Resource> items = resourceList.getItems();
        int size = 0;
        if (items != null) {
            size = items.size();
            for (Resource item : items) {
                handler.handleItem(item);
            }
        }
        handler.onFinished(size);
    }

    public void downloadFile(final String path, final File saveTo, final List<CustomHeader> headerList,
                             final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = call(headerList).getDownloadLink(path);
        new HttpClientIO(client, getAllHeaders(headerList))
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public Link saveFromUrl(final String url, final String serverPath, final List<CustomHeader> headerList)
            throws ServerIOException {
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
            logger.debug("head: startOffset="+startOffset);
        }
        clientIO.uploadFile(link.getHref(), localSource, startOffset, progressListener);
    }

    public Link delete(final String path, final boolean permanently)
            throws ServerIOException, IOException {
        return new HttpClientIO(client, getAllHeaders(null))
                .delete(new QueryBuilder(getUrl() + "/v1/disk/resources")
                        .add("path", path)
                        .add("permanently", permanently)
                        .build());
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
            throws ServerIOException {
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

    public void downloadPublicResource(final String publicKey, final String path, final File saveTo,
                                       final List<CustomHeader> headerList, final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = call(headerList).getPublicResourceDownloadLink(publicKey, path);
        new HttpClientIO(client, getAllHeaders(headerList))
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public Link savePublicResource(final String publicKey, final String path, final String name)
            throws IOException, ServerException {
        return call().savePublicResource(publicKey, path, name);
    }
}