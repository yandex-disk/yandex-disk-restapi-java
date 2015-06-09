/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskInfo;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import retrofit.http.GET;
import retrofit.http.Body;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

import java.io.IOException;

public interface CloudApi {

    @GET("/")
    ApiVersion getApiVersion()
            throws IOException, ServerIOException;

    @GET("/v1/disk/operations/{operation_id}")
    Operation getOperation(@Path("operation_id") String operationId)
            throws IOException, ServerIOException;

    @GET("/v1/disk")
    DiskInfo getDiskInfo(@Query("fields") String fields)
            throws IOException, ServerIOException;

    @GET("/v1/disk/resources")
    Resource getResources(@Query("path") String path, @Query("fields") String fields,
                          @Query("limit") Integer limit, @Query("offset") Integer offset,
                          @Query("sort") String sort, @Query("preview_size") String previewSize,
                          @Query("preview_crop") Boolean previewCrop)
            throws IOException, ServerIOException;

    @GET("/v1/disk/resources/files")
    ResourceList getFlatResourceList(@Query("limit") Integer limit, @Query("media_type") String mediaType,
                                     @Query("offset") Integer offset, @Query("fields") String fields,
                                     @Query("preview_size") String previewSize,
                                     @Query("preview_crop") Boolean previewCrop)
            throws IOException, ServerIOException;

    @GET("/v1/disk/resources/last-uploaded")
    ResourceList getLastUploadedResources(@Query("limit") Integer limit, @Query("media_type") String mediaType,
                                          @Query("offset") Integer offset, @Query("fields") String fields,
                                          @Query("preview_size") String previewSize,
                                          @Query("preview_crop") Boolean previewCrop)
            throws IOException, ServerIOException;

    @PATCH("/v1/disk/resources/")
    Resource patchResource(@Query("path") String path, @Query("fields") String fields,
                           @Body TypedOutput body)
            throws IOException, ServerIOException;

    @GET("/v1/disk/resources/download")
    Link getDownloadLink(@Query("path") String path)
            throws IOException, ServerIOException;

    @POST("/v1/disk/resources/upload")
    Link saveFromUrl(@Query("url") String url, @Query("path") String path)
            throws IOException, ServerIOException;

    @GET("/v1/disk/resources/upload")
    Link getUploadLink(@Query("path") String path, @Query("overwrite") Boolean overwrite)
            throws IOException, ServerIOException;

    @POST("/v1/disk/resources/copy")
    Link copy(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws IOException, ServerIOException;

    @POST("/v1/disk/resources/move")
    Link move(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws IOException, ServerIOException;

    @PUT("/v1/disk/resources")
    Link makeFolder(@Query("path") String path)
            throws IOException, ServerIOException;

    @PUT("/v1/disk/resources/publish")
    Link publish(@Query("path") String path)
            throws IOException, ServerIOException;

    @PUT("/v1/disk/resources/unpublish")
    Link unpublish(@Query("path") String path)
            throws IOException, ServerIOException;

    @GET("/v1/disk/public/resources")
    Resource listPublicResources(@Query("public_key") String publicKey, @Query("path") String path,
                                 @Query("fields") String fields, @Query("limit") Integer limit,
                                 @Query("offset") Integer offset, @Query("sort") String sort,
                                 @Query("preview_size") String previewSize,
                                 @Query("preview_crop") Boolean previewCrop)
            throws IOException, ServerIOException;

    @GET("/v1/disk/public/resources/download")
    Link getPublicResourceDownloadLink(@Query("public_key") String publicKey,
                                       @Query("path") String path)
            throws IOException, ServerIOException;

    @POST("/v1/disk/public/resources/save-to-disk/")
    Link savePublicResource(@Query("public_key") String publicKey, @Query("path") String path,
                            @Query("name") String name)
            throws IOException, ServerIOException;

    @GET("/v1/disk/trash/resources")
    Resource getTrashResources(@Query("path") String path, @Query("fields") String fields,
                               @Query("limit") Integer limit, @Query("offset") Integer offset,
                               @Query("sort") String sort, @Query("preview_size") String previewSize,
                               @Query("preview_crop") Boolean previewCrop)
            throws IOException, ServerIOException;
}
