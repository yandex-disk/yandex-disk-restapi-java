package com.yandex.disk.rest.retrofit;

import com.yandex.disk.rest.exceptions.FileNotModifiedException;
import com.yandex.disk.rest.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.rest.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.rest.exceptions.ServerWebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.DiskMeta;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Resource;

import java.util.Map;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.http.Streaming;

public interface CloudApi {

    @GET("/v1/disk")
    DiskMeta getMeta(@Query("fields") String fields)
            throws WebdavIOException;

    @GET("/v1/disk/resources?limit=0")
    Resource listResources(@Query("path") String path)
            throws WebdavIOException;

    @GET("/v1/disk/resources")
    Resource listResources(@Query("path") String path, @Query("limit") int limit, @Query("offset") int offset, @Query("sort") String sort)
            throws WebdavIOException;

    @GET("/v1/disk/resources/download")
    Link getDownloadLink(@Query("path") String path)
            throws WebdavIOException;

    @GET("/")
    @Streaming
    Response downloadFile(/*@Path("path") String path, */ @QueryMap Map<String, String> queryMap)
            throws FileNotModifiedException, RemoteFileNotFoundException, RangeNotSatisfiableException, ServerWebdavException;

}
