/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.yandex.disk.rest.exceptions.CancelledDownloadException;
import com.yandex.disk.rest.exceptions.DownloadNoSpaceAvailableException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.http.ConflictException;
import com.yandex.disk.rest.exceptions.http.FileNotModifiedException;
import com.yandex.disk.rest.exceptions.http.FileTooBigException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.exceptions.http.InsufficientStorageException;
import com.yandex.disk.rest.exceptions.http.NotFoundException;
import com.yandex.disk.rest.exceptions.http.PreconditionFailedException;
import com.yandex.disk.rest.exceptions.http.RangeNotSatisfiableException;
import com.yandex.disk.rest.exceptions.http.ServiceUnavailableException;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Operation;
import com.yandex.disk.rest.retrofit.ErrorHandlerImpl;
import com.yandex.disk.rest.util.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* package */ class RestClientIO {

    private static final Logger logger = LoggerFactory.getLogger(RestClientIO.class);

    private static final String ETAG_HEADER = "Etag";
    private static final String SHA256_HEADER = "Sha256";
    private static final String SIZE_HEADER = "Size";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_RANGE_HEADER = "Content-Range";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PUT = "PUT";

    private static final Pattern CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("bytes\\D+(\\d+)-\\d+/(\\d+)");

    private OkHttpClient client;
    private List<CustomHeader> commonHeaders;

    /* package */ RestClientIO(OkHttpClient client, List<CustomHeader> commonHeaders) {
        this.client = client;
        this.commonHeaders = commonHeaders;
    }

    private Request.Builder buildRequest() {
        Request.Builder request = new Request.Builder();
        for (CustomHeader header : commonHeaders) {
            request.addHeader(header.getName(), header.getValue());
        }
        return request;
    }

    /* package */ void downloadUrl(final String url, final DownloadListener downloadListener)
            throws IOException, CancelledDownloadException, DownloadNoSpaceAvailableException,
            HttpCodeException {

        Request.Builder req = buildRequest()
                .url(url);

        long length = downloadListener.getLocalLength();
        String ifTag = "If-None-Match";
        if (length >= 0) {
            ifTag = "If-Range";
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes=").append(length).append("-");
            logger.debug("Range: " + contentRange);
            req.addHeader("Range", contentRange.toString());
        }

        String etag = downloadListener.getETag();
        if (etag != null) {
            logger.debug(ifTag + ": " + etag);
            req.addHeader(ifTag, etag);
        }

        Request request = req.build();
        Response response = client
                .newCall(request)
                .execute();

        boolean partialContent = false;
        int code = response.code();
        switch (code) {
            case 200:
                // OK
                break;
            case 206:
                partialContent = true;
                break;
            case 304:
                throw new FileNotModifiedException(code);
            case 404:
                throw new NotFoundException(code);
            case 416:
                throw new RangeNotSatisfiableException(code);
            default:
                throw new HttpCodeException(code);
        }

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        logger.debug("download: contentLength=" + contentLength);

        long loaded;
        if (partialContent) {
            ContentRangeResponse contentRangeResponse = parseContentRangeHeader(response.header("Content-Range"));
            logger.debug("download: contentRangeResponse=" + contentRangeResponse);
            if (contentRangeResponse != null) {
                loaded = contentRangeResponse.getStart();
                contentLength = contentRangeResponse.getSize();
            } else {
                loaded = length;
            }
        } else {
            loaded = 0;
            if (contentLength < 0) {
                contentLength = 0;
            }
        }

        OutputStream os = null;
        try {
            downloadListener.setStartPosition(loaded);
            MediaType contentTypeHeader = responseBody.contentType();
            if (contentTypeHeader != null) {
                downloadListener.setContentType(contentTypeHeader.toString());
            }
            downloadListener.setContentLength(contentLength);

            int count;
            InputStream content = responseBody.byteStream();
            os = downloadListener.getOutputStream(partialContent);
            final byte[] downloadBuffer = new byte[1024];
            while ((count = content.read(downloadBuffer)) != -1) {
                if (downloadListener.hasCancelled()) {
                    logger.info("Downloading " + url + " canceled");
                    client.cancel(request.tag());
                    throw new CancelledDownloadException();
                }
                os.write(downloadBuffer, 0, count);
                loaded += count;
                downloadListener.updateProgress(loaded, contentLength);
            }
        } catch (CancelledDownloadException ex) {
            throw ex;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            client.cancel(request.tag());
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof DownloadNoSpaceAvailableException) {
                throw (DownloadNoSpaceAvailableException) e;
            } else {
                // never happen
                throw new RuntimeException(e);
            }
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                // nothing
            }
            try {
                response.body().close();
            } catch (IOException | NullPointerException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

    private ContentRangeResponse parseContentRangeHeader(String header) {
        if (header == null) {
            return null;
        }
        Matcher matcher = CONTENT_RANGE_HEADER_PATTERN.matcher(header);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new ContentRangeResponse(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
        } catch (IllegalStateException ex) {
            logger.error("parseContentRangeHeader: " + header, ex);
            return null;
        } catch (NumberFormatException ex) {
            logger.error("parseContentRangeHeader: " + header, ex);
            return null;
        }
    }

    /* package */ void uploadFile(final String url, final File file, final long startOffset,
                           final ProgressListener progressListener)
            throws IOException, HttpCodeException {
        logger.debug("uploadFile: put to url: "+url);
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBodyProgress.create(mediaType, file, startOffset,
                progressListener);
        Request.Builder requestBuilder = buildRequest()
                .removeHeader(Credentials.AUTHORIZATION_HEADER)
                .url(url)
                .put(requestBody);
        if (startOffset > 0) {
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes ").append(startOffset).append("-").append(file.length() - 1)
                    .append("/").append(file.length());
            logger.debug(CONTENT_RANGE_HEADER + ": " + contentRange);
            requestBuilder.addHeader(CONTENT_RANGE_HEADER, contentRange.toString());
        }
        Request request = requestBuilder.build();

        Response response = client
                .newCall(request)
                .execute();

        String statusLine = response.message();
        logger.debug("headUrl: " + statusLine + " for url " + url);

        int code = response.code();

        ResponseBody responseBody = response.body();
        responseBody.close();

        switch (code) {
            case 201:
            case 202:
                logger.debug("uploadFile: file uploaded successfully: "+file);
                break;
            case 404:
                throw new NotFoundException(code, null);
            case 409:
                throw new ConflictException(code, null);
            case 412:
                throw new PreconditionFailedException(code, null);
            case 413:
                throw new FileTooBigException(code, null);
            case 503:
                throw new ServiceUnavailableException(code, null);
            case 507:
                throw new InsufficientStorageException(code, null);
            default:
                throw new HttpCodeException(code);
        }
    }

    /* package */ long getUploadedSize(String url, Hash hash)
            throws IOException {

        Request request = buildRequest()
                .removeHeader(Credentials.AUTHORIZATION_HEADER)
                .url(url)
                .head()
                .addHeader(ETAG_HEADER, hash.getMd5())
                .addHeader(SHA256_HEADER, hash.getSha256())
                .addHeader(SIZE_HEADER, String.valueOf(hash.getSize()))
                .build();

        Response response = client
                .newCall(request)
                .execute();

        int code = response.code();
        ResponseBody responseBody = response.body();
        responseBody.close();
        switch (code) {
            case 200:
                return Long.valueOf(response.header(CONTENT_LENGTH_HEADER, "0"));
            default:
                return 0;
        }
    }

    /* package */ Operation getOperation(String url)
            throws IOException, HttpCodeException {
        Response response = call(METHOD_GET, url);
        int code = response.code();
        if (!response.isSuccessful()) {
            throw new HttpCodeException(code);
        }
        return parseJson(response, Operation.class);
    }

    /* package */ Link delete(String url)
            throws IOException, ServerIOException {
        Response response = null;
        try {
            response = call(METHOD_DELETE, url);
            switch (response.code()) {
                case 202:
                    Link result = parseJson(response, Link.class);
                    result.setHttpStatus(Link.HttpStatus.inProgress);
                    return result;
                case 204:
                    close(response);
                    return Link.DONE;
                default:
                    throw ErrorHandlerImpl.createHttpCodeException(response.code(),
                            response.body().byteStream());
            }
        } finally {
            close(response);
        }
    }

    /* package */ Link put(String url)
            throws IOException, ServerIOException {
        Response response = null;
        try {
            response = call(METHOD_PUT, url);
            switch (response.code()) {
                case 201:
                    Link done = parseJson(response, Link.class);
                    done.setHttpStatus(Link.HttpStatus.done);
                    return done;
                case 202:
                    Link inProgress = parseJson(response, Link.class);
                    inProgress.setHttpStatus(Link.HttpStatus.inProgress);
                    return inProgress;
                default:
                    throw ErrorHandlerImpl.createHttpCodeException(response.code(),
                            response.body().byteStream());
            }
        } finally {
            close(response);
        }
    }

    private void close(Response response) throws IOException {
        if (response == null) {
            return;
        }
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return;
        }

        responseBody.close();
    }

    private Response call(String method, String url)
            throws IOException {
        Request request = buildRequest()
                .method(method, null)
                .url(url)
                .build();
        return client.newCall(request)
                .execute();
    }

    private <T> T parseJson(Response response, Class<T> classOfT)
            throws IOException {
        ResponseBody responseBody = null;
        try {
            responseBody = response.body();
            Gson gson = new Gson();
            return gson.fromJson(responseBody.charStream(), classOfT);
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
    }

    private static class ContentRangeResponse {

        private final long start, size;

        ContentRangeResponse(long start, long size) {
            this.start = start;
            this.size = size;
        }

        long getStart() {
            return start;
        }

        long getSize() {
            return size;
        }
    }
}
