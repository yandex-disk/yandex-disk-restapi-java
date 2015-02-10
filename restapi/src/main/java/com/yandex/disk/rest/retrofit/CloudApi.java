package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskCapacity;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CloudApi {

    /**
     * Server API version and build
     */
    @GET("/")
    ApiVersion getApiVersion()
            throws ServerIOException;

    /**
     * Operation status
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/operations.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/operations-docpage/">russian</a></p>
     */
    @GET("/v1/disk/operations/{operation_id}")
    Operation getOperation(@Path("operation_id") String operationId)
            throws ServerIOException;

    /**
     * Data about a user's Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/capacity.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/capacity-docpage/">russian</a></p>
     */
    @GET("/v1/disk")
    DiskCapacity getCapacity(@Query("fields") String fields)
            throws ServerIOException;

    /**
     * Metainformation about a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    @GET("/v1/disk/resources")
    Resource listResources(@Query("path") String path, @Query("fields") String fields,
                           @Query("limit") Integer limit, @Query("offset") Integer offset,
                           @Query("sort") String sort, @Query("preview_size") String previewSize,
                           @Query("preview_crop") Boolean previewCrop)
            throws ServerIOException;


    // TODO https://tech.yandex.ru/disk/api/reference/all-files-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/recent-upload-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/meta-add-docpage/


    /**
     * Downloading a file from Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/content.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/content-docpage/">russian</a></p>
     */
    @GET("/v1/disk/resources/download")
    Link getDownloadLink(@Query("path") String path)
            throws ServerIOException;

    /**
     * Uploading a file to Disk from external resource
     *
     * <br/><br/><tt>TODO link to API reference in english is broken</tt>
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload-ext.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-ext-docpage/">russian</a></p>
     */
    @POST("/v1/disk/resources/upload")
    Link saveFromUrl(@Query("url") String url, @Query("path") String path)
            throws ServerIOException;

    /**
     * Uploading a file to Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-docpage/">russian</a></p>
     */
    @GET("/v1/disk/resources/upload")
    Link getUploadLink(@Query("path") String path, @Query("overwrite") Boolean overwrite)
            throws ServerIOException;

    /**
     * Copying a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/copy.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/copy-docpage/">russian</a></p>
     */
    @POST("/v1/disk/resources/copy")
    Link copy(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws ServerIOException;

    /**
     * Moving a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/move.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/move-docpage/">russian</a></p>
     */
    @POST("/v1/disk/resources/move")
    Link move(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws ServerIOException;

    /**
     * Deleting a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/delete.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/delete-docpage/">russian</a></p>
     */
    @DELETE("/v1/disk/resources")
    Link delete(@Query("path") String path, @Query("permanently") Boolean permanently)
            throws ServerIOException;

    /**
     * Creating a folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/create-folder.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/create-folder-docpage/">russian</a></p>
     */
    @PUT("/v1/disk/resources")
    Link makeFolder(@Query("path") String path)
            throws ServerIOException;

    /**
     * Publishing a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/publish.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/publish-docpage/">russian</a></p>
     */
    @PUT("/v1/disk/resources/publish")
    Link publish(@Query("path") String path)
            throws ServerIOException;

    /**
     * Closing access to a resource
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/publish.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/publish-docpage/">russian</a></p>
     */
    @PUT("/v1/disk/resources/unpublish")
    Link unpublish(@Query("path") String path)
            throws ServerIOException;

    /**
     * Metainformation about a public resource
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/">russian</a></p>
     */
    @GET("/v1/disk/public/resources")
    Resource listPublicResources(@Query("public_key") String publicKey, @Query("path") String path,
                                 @Query("fields") String fields, @Query("limit") Integer limit,
                                 @Query("offset") Integer offset, @Query("sort") String sort,
                                 @Query("preview_size") String previewSize,
                                 @Query("preview_crop") Boolean previewCrop)
            throws ServerIOException;

    /**
     * Downloading a public file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml#download">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/#download">russian</a></p>
     */
    @GET("/v1/disk/public/resources/download")
    Link getPublicResourceDownloadLink(@Query("public_key") String publicKey,
                                       @Query("path") String path)
            throws ServerIOException;

    /**
     * Saving a public file in "Downloads"
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/public.xml#save">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/public-docpage/#save">russian</a></p>
     */
    @POST("/v1/disk/public/resources/save-to-disk/")
    Link savePublicResource(@Query("public_key") String publicKey, @Query("path") String path,
                            @Query("name") String name)
            throws ServerIOException;

    /**
     * Metainformation about a file or folder in the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    @GET("/v1/disk/trash/resources")
    Resource listTrash(@Query("path") String path, @Query("fields") String fields,
                       @Query("limit") Integer limit, @Query("offset") Integer offset,
                       @Query("sort") String sort, @Query("preview_size") String previewSize,
                       @Query("preview_crop") Boolean previewCrop)
            throws ServerIOException;

    /**
     * Cleaning the Trash
     *
     * TODO write test
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-delete.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-delete-docpage/">russian</a></p>
     */
    @DELETE("/v1/disk/trash/resources")
    void dropTrash(@Query("path") String path, Callback<Link> callback)
            throws ServerIOException;

    /**
     * Restoring a file or folder from the Trash
     *
     * TODO write test
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-restore.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-restore-docpage/">russian</a></p>
     */
    @PUT("/v1/disk/trash/resources/restore")
    void restoreTrash(@Query("path") String path, @Query("name") String name,
                      @Query("overwrite") Boolean overwrite, Callback<Link> callback)
            throws ServerIOException;
}
