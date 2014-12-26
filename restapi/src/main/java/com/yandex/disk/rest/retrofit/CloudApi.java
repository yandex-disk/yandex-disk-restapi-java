package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.FileNotModifiedException;
import com.yandex.disk.rest.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.rest.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.rest.exceptions.ServerWebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskMeta;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;

import java.util.Map;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.http.Streaming;

public interface CloudApi {

    @GET("/")
    ApiVersion getApiVersion()
            throws WebdavIOException;

    @GET("/v1/disk/operations/{operation_id}")
    Operation getOperation(@Path("operation_id") String operationId)
            throws WebdavIOException;

    @GET("/v1/disk")
    DiskMeta getDiskMeta(@Query("fields") String fields)
            throws WebdavIOException;

    @GET("/v1/disk/resources")
    Resource listResources(@Query("path") String path, @Query("limit") int limit, @Query("offset") int offset, @Query("sort") String sort)
            throws WebdavIOException;

    @GET("/v1/disk/resources/download")
    Link getDownloadLink(@Query("path") String path)
            throws WebdavIOException;

    @GET("/v1/disk/resources/upload")
    Link getUploadLink(@Query("path") String path, @Query("overwrite") boolean overwrite)
            throws WebdavIOException;

    @GET("/v1/disk/trash/resources")
    Resource listTrash(@Query("path") String path, @Query("fields") String fields,
                       @Query("limit") int limit, @Query("offset") int offset, @Query("sort") String sort,
                       @Query("preview_size") String previewSize)
            throws WebdavIOException;

    @DELETE("/v1/disk/trash/resources")
    Link dropTrash(@Query("path") String path, @Query("fields") String fields)
            throws WebdavIOException;

    @PUT("/v1/disk/trash/resources/restore")
    Link restoreTrash(@Query("path") String path, @Query("fields") String fields, @Query("name") String name,
                      @Query("overwrite") boolean overwrite)
            throws WebdavIOException;

}
