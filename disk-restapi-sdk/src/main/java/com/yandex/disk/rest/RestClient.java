/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;
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
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private static final RestAdapter.LogLevel LOG_LEVEL = logger.isDebugEnabled()
            ? RestAdapter.LogLevel.FULL
            : RestAdapter.LogLevel.NONE;

    private final Credentials credentials;
    private final OkHttpClient client;
    private final String serverURL;
    private final CloudApi cloudApi;
    protected final RestAdapter.Builder builder;

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

        this.builder = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(getUrl())
                .setRequestInterceptor(new RequestInterceptorImpl(credentials.getHeaders()))
                .setErrorHandler(new ErrorHandlerImpl())
                .setLogLevel(LOG_LEVEL);

        this.cloudApi = builder
                .build()
                .create(CloudApi.class);
    }

    /* package */ String getUrl() {
        return serverURL;
    }

    /* package */ OkHttpClient getClient() {
        return client;
    }

    /**
     * Server API version and build
     */
    public ApiVersion getApiVersion()
            throws IOException, ServerIOException {
        return cloudApi.getApiVersion();
    }

    /**
     * Operation status
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/operations.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/operations-docpage/">russian</a></p>
     */
    public Operation getOperation(final String operationId)
            throws IOException, ServerIOException {
        return cloudApi.getOperation(operationId);
    }

    /**
     * Operation status
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/operations.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/operations-docpage/">russian</a></p>
     */
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

    /**
     * Waiting operation to stop
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/operations.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/operations-docpage/">russian</a></p>
     */
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

    /**
     * Data about a user's Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/capacity.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/capacity-docpage/">russian</a></p>
     */
    public DiskInfo getDiskInfo()
            throws IOException, ServerIOException {
        return getDiskInfo(null);
    }

    /**
     * Data about a user's Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/capacity.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/capacity-docpage/">russian</a></p>
     */
    public DiskInfo getDiskInfo(final String fields)
            throws IOException, ServerIOException {
        return cloudApi.getDiskInfo(fields);
    }

    /**
     * Metainformation about a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    public Resource getResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = cloudApi.getResources(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    /**
     * Flat list of all files
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/all-files.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/all-files-docpage/">russian</a></p>
     */
    public ResourceList getFlatResourceList(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = cloudApi.getFlatResourceList(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    /**
     * Latest uploaded files
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/recent-upload.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/recent-upload-docpage/">russian</a></p>
     */
    public ResourceList getLastUploadedResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final ResourceList resourceList = cloudApi.getLastUploadedResources(args.getLimit(), args.getMediaType(),
                args.getOffset(), args.getFields(), args.getPreviewSize(), args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resourceList, args.getParsingHandler());
        }
        return resourceList;
    }

    /**
     * Latest uploaded files
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta-add.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-add-docpage/">russian</a></p>
     */
    public Resource patchResource(final ResourcesArgs args)
            throws ServerIOException, IOException {
        final Resource resource = cloudApi.patchResource(args.getPath(), args.getFields(), args.getBody());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    /**
     * Metainformation about a public resource
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/">russian</a></p>
     */
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

    /**
     * Metainformation about a file or folder in the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    public Resource getTrashResources(final ResourcesArgs args)
            throws IOException, ServerIOException {
        final Resource resource = cloudApi.getTrashResources(args.getPath(), args.getFields(),
                args.getLimit(), args.getOffset(), args.getSort(), args.getPreviewSize(),
                args.getPreviewCrop());
        if (args.getParsingHandler() != null) {
            parseListResponse(resource, args.getParsingHandler());
        }
        return resource;
    }

    /**
     * Cleaning the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-delete.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-delete-docpage/">russian</a></p>
     */
    public Link deleteFromTrash(final String path)
            throws IOException, ServerIOException {
        return new RestClientIO(client, credentials.getHeaders())
                .delete(new QueryBuilder(getUrl() + "/v1/disk/trash/resources")
                        .add("path", path)
                        .build());
    }

    /**
     * Restoring a file or folder from the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-restore.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-restore-docpage/">russian</a></p>
     */
    public Link restoreFromTrash(final String path, final String name, final Boolean overwrite)
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

    /**
     * Downloading a file from Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/content.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/content-docpage/">russian</a></p>
     */
    public void downloadFile(final String path, final File saveTo, final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    /**
     * Downloading a file from Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/content.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/content-docpage/">russian</a></p>
     */
    public void downloadFile(final String path, final DownloadListener downloadListener)
            throws IOException, ServerException {
        Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), downloadListener);
    }

    /**
     * Uploading a file to Disk from external resource
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload-ext.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-ext-docpage/">russian</a></p>
     */
    public Link saveFromUrl(final String url, final String serverPath)
            throws ServerIOException, IOException {
        return cloudApi.saveFromUrl(url, serverPath);
    }

    /**
     * Uploading a file to Disk: get Link to upload
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-docpage/">russian</a></p>
     */
    public Link getUploadLink(final String serverPath, final boolean overwrite)
            throws ServerIOException, WrongMethodException, IOException {
        Link link = cloudApi.getUploadLink(serverPath, overwrite);
        if (!"PUT".equalsIgnoreCase(link.getMethod())) {
            throw new WrongMethodException("Method in Link object is not PUT");
        }
        return link;
    }

    /**
     * Uploading a file to Disk: upload a file
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-docpage/">russian</a></p>
     */
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

    /**
     * Deleting a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/delete.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/delete-docpage/">russian</a></p>
     */
    public Link delete(final String path, final boolean permanently)
            throws ServerIOException, IOException {
        return new RestClientIO(client, credentials.getHeaders())
                .delete(new QueryBuilder(getUrl() + "/v1/disk/resources")
                        .add("path", path)
                        .add("permanently", permanently)
                        .build());
    }

    /**
     * Creating a folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/create-folder.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/create-folder-docpage/">russian</a></p>
     */
    public Link makeFolder(final String path)
            throws ServerIOException, IOException {
        return cloudApi.makeFolder(path);
    }

    /**
     * Copying a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/copy.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/copy-docpage/">russian</a></p>
     */
    public Link copy(final String from, final String path, final boolean overwrite)
            throws ServerIOException, IOException {
        return cloudApi.copy(from, path, overwrite);
    }

    /**
     * Moving a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/move.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/move-docpage/">russian</a></p>
     */
    public Link move(final String from, final String path, final boolean overwrite)
            throws ServerIOException, IOException {
        return cloudApi.move(from, path, overwrite);
    }

    /**
     * Publishing a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/publish.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/publish-docpage/">russian</a></p>
     */
    public Link publish(final String path)
            throws ServerIOException, IOException {
        return cloudApi.publish(path);
    }

    /**
     * Closing access to a resource
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/publish.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/publish-docpage/">russian</a></p>
     */
    public Link unpublish(final String path)
            throws ServerIOException, IOException {
        return cloudApi.unpublish(path);
    }

    /**
     * Downloading a public file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml#download">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/#download">russian</a></p>
     */
    public void downloadPublicResource(final String publicKey, final String path, final File saveTo,
                                       final ProgressListener progressListener)
            throws IOException, ServerException {
        Link link = cloudApi.getPublicResourceDownloadLink(publicKey, path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(link.getHref(), new FileDownloadListener(saveTo, progressListener));
    }

    /**
     * Saving a public file in "Downloads"
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml#save">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/#save">russian</a></p>
     */
    public Link savePublicResource(final String publicKey, final String path, final String name)
            throws IOException, ServerException {
        return cloudApi.savePublicResource(publicKey, path, name);
    }
}