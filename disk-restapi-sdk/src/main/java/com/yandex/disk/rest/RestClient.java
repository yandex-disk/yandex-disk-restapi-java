/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.WrongMethodException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskInfo;
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
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private static final RestAdapter.LogLevel LOG_LEVEL = logger.isDebugEnabled()
            ? RestAdapter.LogLevel.FULL
            : RestAdapter.LogLevel.NONE;

    private final Credentials credentials;
    private final OkHttpClient client;
    private final String serverURL;
    private final CloudApi cloudApi;

    public RestClient(final Credentials credentials) {
        this(credentials, OkHttpClientFactory.makeClient());
    }

    public RestClient(final Credentials credentials, final OkHttpClient client) {
        this(credentials, client, "https://cloud-api.yandex.net");
    }

    public RestClient(final Credentials credentials, final OkHttpClient client, final String serverUrl) {
        this.credentials = credentials;
        this.client = client;
        try {
            this.serverURL = new URL(serverUrl).toExternalForm();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        this.cloudApi = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(getUrl())
                .setRequestInterceptor(new RequestInterceptorImpl(credentials.getHeaders()))
                .setErrorHandler(new ErrorHandlerImpl())
                .setLogLevel(LOG_LEVEL)
                .build()
                .create(CloudApi.class);
    }

    /* package */ String getUrl() {
        return serverURL;
    }

    public ApiVersion getApiVersion()
            throws IOException, ServerIOException {
        return cloudApi.getApiVersion();
    }

    public Operation getOperation(final String operationId)
            throws IOException, ServerIOException {
        return cloudApi.getOperation(operationId);
    }

    public Operation getOperation(final Link link)
            throws IOException, WrongMethodException, HttpCodeException {
        if (!"GET".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not GET");
        }
        Operation operation = new RestClientIO(client, credentials.getHeaders())
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

    public DiskInfo getDiskInfo()
            throws IOException, ServerIOException {
        return getDiskInfo(null);
    }

    public DiskInfo getDiskInfo(final String fields)
            throws IOException, ServerIOException {
        return cloudApi.getDiskInfo(fields);
    }

    public Resource listResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = cloudApi.listResources(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public ResourceList flatListResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = cloudApi.flatListResources(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    public ResourceList uploadedListResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = cloudApi.uploadedListResources(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    public Resource patchResource(final ResourcesArgs args)
            throws ServerIOException, NetworkIOException {
        final Resource resource = cloudApi.patchResource(args.getPath(), args.getFields(), args.getBody());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public Resource listPublicResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = cloudApi.listPublicResources(args.getPublicKey(), args.getPath(),
                args.getFields(), args.getLimit(), args.getOffset(), args.getSort(),
                args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public Resource listTrash(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = cloudApi.listTrash(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    public Link dropTrash(final String path)
            throws IOException, ServerIOException, URISyntaxException {
        return new RestClientIO(client, credentials.getHeaders())
                .delete(new QueryBuilder(getUrl() + "/v1/disk/trash/resources")
                        .add("path", path)
                        .build());
    }

    public Link restoreTrash(final String path, final String name, final Boolean overwrite)
            throws IOException, ServerIOException {
        return new RestClientIO(client, credentials.getHeaders())
                .put(new QueryBuilder(getUrl() + "/v1/disk/trash/resources/restore")
                        .add("path", path)
                        .add("name", name)
                        .add("overwrite", overwrite)
                        .build());
    }

    private void parseListResponse(final Resource resource, final ResourcesHandler handler) {
        handler.handleSelf(resource);
        ResourceList items = resource.getResourceList();
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

    public void downloadFile(final String path, final File saveTo, final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public void downloadFile(final String path, final DownloadListener downloadListener)
            throws IOException, ServerException {
        Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), downloadListener);
    }

    public Link saveFromUrl(final String url, final String serverPath)
            throws ServerIOException, NetworkIOException {
        return cloudApi.saveFromUrl(url, serverPath);
    }

    public Link getUploadLink(final String serverPath, final boolean overwrite)
            throws ServerIOException, WrongMethodException, NetworkIOException {
        Link link = cloudApi.getUploadLink(serverPath, overwrite);
        if (!"PUT".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not PUT");
        }
        return link;
    }

    public void uploadFile(final Link link, final boolean resumeUpload, final File localSource,
                           final ProgressListener progressListener)
            throws IOException, ServerException {
        RestClientIO clientIO = new RestClientIO(client, credentials.getHeaders());
        long startOffset = 0;
        if (resumeUpload) {
            Hash hash = Hash.getHash(localSource);
            startOffset = clientIO.getUploadedSize(link.getHref(), hash);
            logger.debug("head: startOffset=" + startOffset);
        }
        clientIO.uploadFile(link.getHref(), localSource, startOffset, progressListener);
    }

    public Link delete(final String path, final boolean permanently)
            throws ServerIOException, IOException {
        return new RestClientIO(client, credentials.getHeaders())
                .delete(new QueryBuilder(getUrl() + "/v1/disk/resources")
                        .add("path", path)
                        .add("permanently", permanently)
                        .build());
    }

    public Link makeFolder(final String path)
            throws ServerIOException, NetworkIOException {
        return cloudApi.makeFolder(path);
    }

    public Link copy(final String from, final String path, final boolean overwrite)
            throws ServerIOException, NetworkIOException {
        return cloudApi.copy(from, path, overwrite);
    }

    public Link move(final String from, final String path, final boolean overwrite)
            throws ServerIOException, NetworkIOException {
        return cloudApi.move(from, path, overwrite);
    }

    public Link publish(final String path)
            throws ServerIOException, NetworkIOException {
        return cloudApi.publish(path);
    }

    public Link unpublish(final String path)
            throws ServerIOException, NetworkIOException {
        return cloudApi.unpublish(path);
    }

    public void downloadPublicResource(final String publicKey, final String path, final File saveTo,
                                       final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = cloudApi.getPublicResourceDownloadLink(publicKey, path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    public Link savePublicResource(final String publicKey, final String path, final String name)
            throws IOException, ServerException {
        return cloudApi.savePublicResource(publicKey, path, name);
    }
}