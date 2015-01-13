package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskCapacity;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CloudApi {

    /**
     * Server API version and build
     */
    @GET("/")
    ApiVersion getApiVersion()
            throws WebdavIOException;

    /**
     * Operation status
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/operations.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/operations-docpage/">russian</a></p>
     */
    @GET("/v1/disk/operations/{operation_id}")
    Operation getOperation(@Path("operation_id") String operationId)
            throws WebdavIOException;

    /**
     * Data about a user's Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/capacity.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/capacity-docpage/">russian</a></p>
     */
    @GET("/v1/disk")
    DiskCapacity getCapacity(@Query("fields") String fields)
            throws WebdavIOException;

    /**
     * Metainformation about a file or folder
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    @GET("/v1/disk/resources")
    Resource listResources(@Query("path") String path, @Query("fields") String fields,
                           @Query("limit") int limit, @Query("offset") int offset, @Query("sort") String sort,
                           @Query("preview_size") String previewSize)
            throws WebdavIOException;


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
            throws WebdavIOException;


    // TODO https://tech.yandex.ru/disk/api/reference/upload-ext-docpage/


    /**
     * Uploading a file to Disk
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/upload.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/upload-docpage/">russian</a></p>
     */
    @GET("/v1/disk/resources/upload")
    Link getUploadLink(@Query("path") String path, @Query("overwrite") boolean overwrite)
            throws WebdavIOException;


    // TODO https://tech.yandex.ru/disk/api/reference/copy-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/move-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/delete-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/create-folder-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/publish-docpage/

    // TODO https://tech.yandex.ru/disk/api/reference/public-docpage/

    /**
     * Metainformation about a file or folder in the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/meta.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/meta-docpage/">russian</a></p>
     */
    @GET("/v1/disk/trash/resources")
    Resource listTrash(@Query("path") String path, @Query("fields") String fields,
                       @Query("limit") int limit, @Query("offset") int offset, @Query("sort") String sort,
                       @Query("preview_size") String previewSize)
            throws WebdavIOException;

    /**
     * Cleaning the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-delete.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-delete-docpage/">russian</a></p>
     */
    @DELETE("/v1/disk/trash/resources")
    Link dropTrash(@Query("path") String path)
            throws WebdavIOException;

    /**
     * Restoring a file or folder from the Trash
     *
     * @see <p>API reference <a href="http://api.yandex.com/disk/api/reference/trash-restore.xml">english</a>,
     * <a href="https://tech.yandex.ru/disk/api/reference/trash-restore-docpage/">russian</a></p>
     */
    @PUT("/v1/disk/trash/resources/restore")
    Link restoreTrash(@Query("path") String path, @Query("name") String name, @Query("overwrite") boolean overwrite)
            throws WebdavIOException;

}
