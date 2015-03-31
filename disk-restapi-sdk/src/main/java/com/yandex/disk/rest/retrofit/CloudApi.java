/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.ApiVersion;
import com.yandex.disk.rest.json.DiskInfo;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

public interface CloudApi {

    @GET("/")
    ApiVersion getApiVersion()
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/operations/{operation_id}")
    Operation getOperation(@Path("operation_id") String operationId)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk")
    DiskInfo getDiskInfo(@Query("fields") String fields)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/resources")
    Resource getResources(@Query("path") String path, @Query("fields") String fields,
                          @Query("limit") Integer limit, @Query("offset") Integer offset,
                          @Query("sort") String sort, @Query("preview_size") String previewSize,
                          @Query("preview_crop") Boolean previewCrop)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/resources/files")
    ResourceList getFlatResourceList(@Query("limit") Integer limit, @Query("media_type") String mediaType,
                                     @Query("offset") Integer offset, @Query("fields") String fields,
                                     @Query("preview_size") String previewSize,
                                     @Query("preview_crop") Boolean previewCrop)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/resources/last-uploaded")
    ResourceList getLastUploadedResources(@Query("limit") Integer limit, @Query("media_type") String mediaType,
                                          @Query("offset") Integer offset, @Query("fields") String fields,
                                          @Query("preview_size") String previewSize,
                                          @Query("preview_crop") Boolean previewCrop)
            throws NetworkIOException, ServerIOException;

    @PATCH("/v1/disk/resources/")
    Resource patchResource(@Query("path") String path, @Query("fields") String fields,
                           @Body TypedOutput body)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/resources/download")
    Link getDownloadLink(@Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @POST("/v1/disk/resources/upload")
    Link saveFromUrl(@Query("url") String url, @Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/resources/upload")
    Link getUploadLink(@Query("path") String path, @Query("overwrite") Boolean overwrite)
            throws NetworkIOException, ServerIOException;

    @POST("/v1/disk/resources/copy")
    Link copy(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws NetworkIOException, ServerIOException;

    @POST("/v1/disk/resources/move")
    Link move(@Query("from") String from, @Query("path") String path,
              @Query("overwrite") Boolean overwrite)
            throws NetworkIOException, ServerIOException;

    @PUT("/v1/disk/resources")
    Link makeFolder(@Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @PUT("/v1/disk/resources/publish")
    Link publish(@Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @PUT("/v1/disk/resources/unpublish")
    Link unpublish(@Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/public/resources")
    Resource listPublicResources(@Query("public_key") String publicKey, @Query("path") String path,
                                 @Query("fields") String fields, @Query("limit") Integer limit,
                                 @Query("offset") Integer offset, @Query("sort") String sort,
                                 @Query("preview_size") String previewSize,
                                 @Query("preview_crop") Boolean previewCrop)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/public/resources/download")
    Link getPublicResourceDownloadLink(@Query("public_key") String publicKey,
                                       @Query("path") String path)
            throws NetworkIOException, ServerIOException;

    @POST("/v1/disk/public/resources/save-to-disk/")
    Link savePublicResource(@Query("public_key") String publicKey, @Query("path") String path,
                            @Query("name") String name)
            throws NetworkIOException, ServerIOException;

    @GET("/v1/disk/trash/resources")
    Resource getTrashResources(@Query("path") String path, @Query("fields") String fields,
                               @Query("limit") Integer limit, @Query("offset") Integer offset,
                               @Query("sort") String sort, @Query("preview_size") String previewSize,
                               @Query("preview_crop") Boolean previewCrop)
            throws NetworkIOException, ServerIOException;
}
