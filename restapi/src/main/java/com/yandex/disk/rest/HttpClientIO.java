package com.yandex.disk.rest;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.yandex.disk.rest.exceptions.CancelledDownloadException;
import com.yandex.disk.rest.exceptions.DownloadNoSpaceAvailableException;
import com.yandex.disk.rest.exceptions.FileModifiedException;
import com.yandex.disk.rest.exceptions.FileNotModifiedException;
import com.yandex.disk.rest.exceptions.IntermediateFolderNotExistException;
import com.yandex.disk.rest.exceptions.PreconditionFailedException;
import com.yandex.disk.rest.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.rest.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.rest.exceptions.ServerWebdavException;
import com.yandex.disk.rest.exceptions.UnknownServerWebdavException;
import com.yandex.disk.rest.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.rest.exceptions.WebdavUserNotInitialized;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientIO {

    private static final String TAG = "Download";

    private OkHttpClient client;
    private List<CustomHeader> commonHeaders;

    public HttpClientIO(HttpClient client, List<CustomHeader> commonHeaders) {
        this.client = client.getClient();
        this.commonHeaders = commonHeaders;
    }

    private Request.Builder buildRequest() {
        Request.Builder request = new Request.Builder();
        for (CustomHeader header : commonHeaders) {
            request.addHeader(header.getName(), header.getValue());
        }
        return request;
    }

    public void downloadUrl(final String url, final List<CustomHeader> headerList, final DownloadListener downloadListener)
            throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException,
            CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, RemoteFileNotFoundException,
            DownloadNoSpaceAvailableException, RangeNotSatisfiableException, FileModifiedException {

        Request.Builder req = buildRequest()
                .url(url);

        long length = downloadListener.getLocalLength();
        String ifTag = "If-None-Match";
        if (length >= 0) {
            ifTag = "If-Range";
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes=").append(length).append("-");
            Log.d(TAG, "Range: " + contentRange);
            req.addHeader("Range", contentRange.toString());
        }

        String etag = downloadListener.getETag();
        if (etag != null) {
            Log.d(TAG, ifTag + ": " + etag);
            req.addHeader(ifTag, etag);
        }

        addHeaders(req, headerList);

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
//                consumeContent(httpResponse);
                throw new FileNotModifiedException();
            case 404:
//                consumeContent(httpResponse);
                throw new RemoteFileNotFoundException("error while downloading file " + url);
            case 416:
//                consumeContent(httpResponse);
                throw new RangeNotSatisfiableException("error while downloading file " + url);
            default:
//                consumeContent(httpResponse);
//                checkStatusCodes(httpResponse, "GET '" + url + "'");
                throw new ServerWebdavException("error while downloading: code=" + code + " file " + url);
//                break;
        }

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        Log.d(TAG, "download: contentLength=" + contentLength);

        long loaded;
        if (partialContent) {
            ContentRangeResponse contentRangeResponse = parseContentRangeHeader(response.header("Content-Range"));
            Log.d(TAG, "download: contentRangeResponse=" + contentRangeResponse);
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

/*
        TODO XXX

        String serverEtag = (String) httpContext.getAttribute(ATTR_ETAG_FROM_REDIRECT);
        if (!partialContent) {
            downloadListener.setEtag(serverEtag);
        } else {
            if (serverEtag != null && !serverEtag.equals(etag)) {
                response.consumeContent();
                throw new FileModifiedException("file changed, new etag is '" + serverEtag  +"'");
            } else {
                //Etag hasn't changed
            }
        }
*/

        OutputStream os = null;
        try {
            downloadListener.setStartPosition(loaded);
            MediaType contentTypeHeader = responseBody.contentType();
            if (contentTypeHeader != null) {
                downloadListener.setContentType(contentTypeHeader.toString());  // TODO XXX untested
            }
            downloadListener.setContentLength(contentLength);

            int count;
            InputStream content = responseBody.byteStream();
            os = downloadListener.getOutputStream(partialContent);
            final byte[] downloadBuffer = new byte[1024];
            while ((count = content.read(downloadBuffer)) != -1) {
                if (downloadListener.hasCancelled()) {
                    Log.i(TAG, "Downloading " + url + " canceled");
                    client.cancel(req);  // TODO XXX untested get.abort();
                    throw new CancelledDownloadException();
                }
                os.write(downloadBuffer, 0, count);
                loaded += count;
                downloadListener.updateProgress(loaded, contentLength);
            }
        } catch (CancelledDownloadException ex) {
            throw ex;
        } catch (Exception e) {
            Log.w(TAG, e);
            client.cancel(req);  // TODO XXX untested get.abort();
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
//            try {
//                response.consumeContent();
//            } catch (IOException e) {
//                Log.w(TAG, e);
//            }
        }
    }

    protected void addHeaders(Request.Builder req, List<CustomHeader> headerList) {
        if (headerList != null) {
            for (CustomHeader header : headerList) {
                req.addHeader(header.getName(), header.getValue());
            }
        }
    }

    private static Pattern CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("bytes\\D+(\\d+)-\\d+/(\\d+)");

    private ContentRangeResponse parseContentRangeHeader(String header) {
        if (header == null) {
            return null;
        }
//        Log.d(TAG, header.getName()+": "+header.getValue());
        Matcher matcher = CONTENT_RANGE_HEADER_PATTERN.matcher(header);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new ContentRangeResponse(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
        } catch (IllegalStateException ex) {
            Log.d(TAG, "parseContentRangeHeader: " + header, ex);
            return null;
        } catch (NumberFormatException ex) {
            Log.d(TAG, "parseContentRangeHeader: " + header, ex);
            return null;
        }
    }

    public void uploadFile(final String url, final File file, final ProgressListener progressListener)
            throws IntermediateFolderNotExistException, IOException, WebdavUserNotInitialized, PreconditionFailedException,
            WebdavNotAuthorizedException, ServerWebdavException, UnknownServerWebdavException {
        Log.d(TAG, "uploadFile: put to url: "+url);

        MediaType mediaType = MediaType.parse("application/octet-stream");  // TODO
        RequestBody requestBody = RequestBody.create(mediaType, file);      // TODO use progressListener
        Request request = buildRequest()
                .removeHeader(TransportClient.AUTHORIZATION_HEADER)
                .url(url)
                .put(requestBody)
                .build();

        Response response = client
                .newCall(request)
                .execute();
        Log.d(TAG, "response: "+response);

        int code = response.code();
        switch (code) {
            case 201:
                // OK
                Log.d(TAG, "File uploaded successfully: "+file);
                break;
            default:
                throw new ServerWebdavException("error while downloading: code=" + code + " file " + url);
        }

        ResponseBody responseBody = response.body();
        Log.d(TAG, "upload: " + responseBody.string());
    }
}
