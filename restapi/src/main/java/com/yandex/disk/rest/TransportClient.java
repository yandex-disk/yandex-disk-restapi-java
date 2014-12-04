package com.yandex.disk.rest;

import com.android.exchange.utility.UriCodec;
import com.yandex.disk.rest.exceptions.CancelledDownloadException;
import com.yandex.disk.rest.exceptions.DownloadNoSpaceAvailableException;
import com.yandex.disk.rest.exceptions.FileModifiedException;
import com.yandex.disk.rest.exceptions.FileNotModifiedException;
import com.yandex.disk.rest.exceptions.PreconditionFailedException;
import com.yandex.disk.rest.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.rest.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.rest.exceptions.ServerWebdavException;
import com.yandex.disk.rest.exceptions.UnknownServerWebdavException;
import com.yandex.disk.rest.exceptions.WebdavClientInitException;
import com.yandex.disk.rest.exceptions.WebdavException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.rest.exceptions.WebdavUserNotInitialized;
import com.yandex.disk.rest.json.DiskMeta;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;
import com.yandex.disk.rest.retrofit.CloudApi;
import com.yandex.disk.rest.retrofit.RetrofitLocationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.mime.TypedInput;

public class TransportClient {

    private static final String TAG = "TransportClient";

    private static final RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.FULL;

    private static final int NETWORK_TIMEOUT = 30000;

    private static final String USER_AGENT = "Cloud API Android Client Example/1.0";
    private static final String ATTR_ETAG_FROM_REDIRECT = "yandex.etag-from-redirect";

    private static URL serverURL;

    static {
        try {
            serverURL = new URL("https://cloud-api.yandex.net:443");
//            serverURL = new URL("http://127.0.0.1:80");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Credentials creds;
    private MyOkClient client;

    public TransportClient(final Credentials credentials, final int networkTimeout)
            throws WebdavClientInitException {
        this.creds = credentials;
        this.client = new MyOkClient();
    }

    public static TransportClient getInstance(final Credentials credentials)
            throws WebdavClientInitException {
        return new TransportClient(credentials, NETWORK_TIMEOUT);
    }

    private String getUrl() {
        return serverURL.toExternalForm();
    }

    private RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", USER_AGENT);
            request.addHeader("Authorization", "OAuth " + creds.getToken());
        }
    };

    private RestAdapter.Builder getRestAdapterBuilder() {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(new ErrorHandlerImpl())
                .setLogLevel(LOG_LEVEL);
    }

    public DiskMeta getMeta()
            throws IOException, WebdavIOException {
        return getMeta(null);
    }

    public DiskMeta getMeta(final String fields)
            throws IOException, WebdavIOException {
        return getRestAdapterBuilder().build()
                .create(CloudApi.class)
                .getMeta(fields);
    }

    public void getList(final String path, final int itemsPerPage, final ListParsingHandler handler)
            throws IOException, WebdavIOException {
        RestAdapter restAdapter = getRestAdapterBuilder().build();
        CloudApi service = restAdapter.create(CloudApi.class);
        Resource resource = service.listResources(path /*, itemsPerPage, 0, null*/ );
        parseListResponse(resource, handler);
    }

    private void parseListResponse(final Resource resource, final ListParsingHandler handler) {
        ResourceList items = resource.getItems();
        int size = items.getItems().size();
        Log.d(TAG, "parseListResponse: size=" + size);
        for (Resource item : items.getItems()) {
            parseListResponseItem(item, handler);
        }
        handler.onPageFinished(size);
    }

    private void parseListResponseItem(final Resource item, final ListParsingHandler handler) {
        Log.d(TAG, "parseListResponseItem: " + item);
        ListItem.Builder builder = new ListItem.Builder();
        builder.setDisplayName(item.getName());
        builder.setFullPath(item.getPath().substring("disk:".length()));
        builder.setContentLength(item.getSize());
        builder.setContentType(item.getMimeType());
        builder.setEtag(item.getMd5());
        builder.setPublicUrl(item.getPublicUrl());
        if ("dir".equals(item.getType())) {
            builder.addCollection();
        }
        builder.setVisible(true);
        ListItem listItem = builder.build();
        Log.d(TAG, "parseListResponseItem: " + listItem);
        handler.handleItem(listItem);
    }

    public void downloadFile(final String path, final File saveTo, final ProgressListener progressListener)
            throws IOException, WebdavException {
        RestAdapter restAdapter = getRestAdapterBuilder().build();
        CloudApi service = restAdapter.create(CloudApi.class);
        Link link = service.getDownloadLink(path);

        downloadUrl(link.getHref(), null, new DownloadListener() {
            @Override
            public OutputStream getOutputStream(boolean append)
                    throws FileNotFoundException {
                return new FileOutputStream(saveTo, append);
            }

            @Override
            public void updateProgress(long loaded, long total) {
                if (progressListener != null) {
                    progressListener.updateProgress(loaded, total);
                }
            }

            @Override
            public boolean hasCancelled() {
                return progressListener != null && progressListener.hasCancelled();
            }
        });
    }

    private void downloadUrl(final String url, final List<CustomHeader> headerList, final DownloadListener downloadListener)
            throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException,
            CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, RemoteFileNotFoundException,
            DownloadNoSpaceAvailableException, RangeNotSatisfiableException, FileModifiedException {

        final List<CustomHeader> headers = new ArrayList<CustomHeader>(/* TODO add headerList */);

        long length = downloadListener.getLocalLength();
        String ifTag = "If-None-Match";
        if (length >= 0) {
            ifTag = "If-Range";
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes=").append(length).append("-");
            Log.d(TAG, "Range: " + contentRange);
            headers.add(new CustomHeader("Range", contentRange.toString()));
        }

        String etag = downloadListener.getETag();
        if (etag != null) {
            Log.d(TAG, ifTag + ": " + etag);
            headers.add(new CustomHeader(ifTag, etag));
        }

//        final Uri uri = Uri.parse(url);
        final URI uri = URI.create(url);   // TODO XXX check it
        String endpoint = new URL(uri.getScheme(), uri.getHost(), uri.getPort(), "").toExternalForm();
//        String endpoint = "http://127.0.0.1:80";
        Log.d(TAG, "endpoint: '" + endpoint + "'");

        StringBuilder sb = new StringBuilder(endpoint);
//        for (String ps : uri.getPathSegments()) {
        for (String ps : uri.getPath().split("/")) {    // TODO XXX check it
            sb.append('/').append(ps);
        }
        String path = sb.toString();
        Log.d(TAG, "path: '" + path + "'");

        Map<String, String> queryMap = new HashMap<>();
        for (String name : getQueryParameterNames(uri)) {
            queryMap.put(name, getQueryParameter(uri, name));
        }
        Log.d(TAG, "queryMap: " + queryMap);

        RestAdapter.Builder req = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(path)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        requestInterceptor.intercept(request);
                        for (CustomHeader header : headers) {
                            request.addHeader(header.getName(), header.getValue());
                        }
//                        request.addHeader("Host", uri.getHost());  // for endpoint 127.0.0.1:80
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        retrofit.client.Response response = retrofitError.getResponse();
                        if (response != null) {
                            int code = response.getStatus();
                            switch (code) {
                                case 302:
                                    String location = getHeader(response.getHeaders(), "Location");
                                    Log.d(TAG, "Location[1]: " + location);
                                    try {
                                        downloadUrl(location, headerList, downloadListener);
                                    } catch (Throwable ex) {
                                        return ex;
                                    }
                                    return new RetrofitLocationException();
                                case 304:
                                    return new FileNotModifiedException();
                                case 404:
                                    return new RemoteFileNotFoundException("error while downloading file " + url);
                                case 416:
                                    return new RangeNotSatisfiableException("error while downloading file " + url);
                                default:
                                    // checkStatusCodes(httpResponse, "GET '" + url + "'");
                                    return new ServerWebdavException("error while downloading: code=" + code + " file " + url);
                            }
                        }
                        return retrofitError;  // TODO XXX
                    }
                })
                .setLogLevel(LOG_LEVEL);

        RestAdapter restAdapter = req.build();
        CloudApi service = restAdapter.create(CloudApi.class);
        retrofit.client.Response response;
        try {
            response = service.downloadFile(/*path, */queryMap);
        } catch (RetrofitLocationException ex) {
            return;
        }

        boolean partialContent = false;
        int code = response.getStatus();
        switch (code) {
            case 200:
                // OK
                break;
            case 206:
                partialContent = true;
                break;
            default:
                throw new ServerWebdavException("error while downloading: code="+code+" file " + url);
        }

        TypedInput responseBody = response.getBody();
        long contentLength = responseBody.length();
        Log.d(TAG, "download: contentLength=" + contentLength);

        long loaded;
        if (partialContent) {
            ContentRangeResponse contentRangeResponse = parseContentRangeHeader(getHeader(response.getHeaders(), "Content-Range"));
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
            String contentTypeHeader = responseBody.mimeType();
            if (contentTypeHeader != null) {
                downloadListener.setContentType(contentTypeHeader);
            }
            downloadListener.setContentLength(contentLength);

            int count;
            InputStream content = responseBody.in();
            os = downloadListener.getOutputStream(partialContent);
            final byte[] downloadBuffer = new byte[1024];
            while ((count = content.read(downloadBuffer)) != -1) {
                if (downloadListener.hasCancelled()) {
                    Log.i(TAG, "Downloading " + url + " canceled");
//                    client.cancel(req);  // TODO XXX untested get.abort();
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
//            client.cancel(req);  // TODO XXX untested get.abort();
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

    private String getHeader(List<Header> headerList, String name) {
        if (headerList == null || headerList.size() == 0) {
            return null;
        }
        for (Header header : headerList) {
            if (name.equals(header.getName())) {
                return header.getValue();
            }
        }
        return null;
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

    /**
     * Returns a set of the unique names of all query parameters. Iterating
     * over the set will return the names in order of their first occurrence.
     *
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     *
     * @return a set of decoded names
     */
    public static Set<String> getQueryParameterNames(final URI uri) {
        String query = uri.getRawQuery();
        if (query == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(UriCodec.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

    /**
     * Searches the query string for the first value with the given key.
     *
     * <p><strong>Warning:</strong> Prior to Ice Cream Sandwich, this decoded
     * the '+' character as '+' rather than ' '.
     *
     * @param key which will be encoded
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @throws NullPointerException if key is null
     * @return the decoded value or null if no parameter is found
     */
    public static  String getQueryParameter(final URI uri, final String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        final String query = uri.getRawQuery();
        if (query == null) {
            return null;
        }

        final String encodedKey = encode(key, null);
        final int length = query.length();
        int start = 0;
        do {
            int nextAmpersand = query.indexOf('&', start);
            int end = nextAmpersand != -1 ? nextAmpersand : length;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            if (separator - start == encodedKey.length()
                    && query.regionMatches(start, encodedKey, 0, encodedKey.length())) {
                if (separator == end) {
                    return "";
                } else {
                    String encodedValue = query.substring(separator + 1, end);
                    return UriCodec.decode(encodedValue, true, StandardCharsets.UTF_8/*, false*/);
                }
            }

            // Move start to end of name.
            if (nextAmpersand != -1) {
                start = nextAmpersand + 1;
            } else {
                break;
            }
        } while (true);
        return null;
    }

    /** Index of a component which was not found. */
    private final static int NOT_FOUND = -1;

    /** Default encoding. */
    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    /**
     * Encodes characters in the given string as '%'-escaped octets
     * using the UTF-8 scheme. Leaves letters ("A-Z", "a-z"), numbers
     * ("0-9"), and unreserved characters ("_-!.~'()*") intact. Encodes
     * all other characters.
     *
     * @param s string to encode
     * @return an encoded version of s suitable for use as a URI component,
     *  or null if s is null
     */
    public static String encode(String s) {
        return encode(s, null);
    }

    /**
     * Encodes characters in the given string as '%'-escaped octets
     * using the UTF-8 scheme. Leaves letters ("A-Z", "a-z"), numbers
     * ("0-9"), and unreserved characters ("_-!.~'()*") intact. Encodes
     * all other characters with the exception of those specified in the
     * allow argument.
     *
     * @param s string to encode
     * @param allow set of additional characters to allow in the encoded form,
     *  null if no characters should be skipped
     * @return an encoded version of s suitable for use as a URI component,
     *  or null if s is null
     */
    public static String encode(String s, String allow) {
        if (s == null) {
            return null;
        }

        // Lazily-initialized buffers.
        StringBuilder encoded = null;

        int oldLength = s.length();

        // This loop alternates between copying over allowed characters and
        // encoding in chunks. This results in fewer method calls and
        // allocations than encoding one character at a time.
        int current = 0;
        while (current < oldLength) {
            // Start in "copying" mode where we copy over allowed chars.

            // Find the next character which needs to be encoded.
            int nextToEncode = current;
            while (nextToEncode < oldLength
                    && isAllowed(s.charAt(nextToEncode), allow)) {
                nextToEncode++;
            }

            // If there's nothing more to encode...
            if (nextToEncode == oldLength) {
                if (current == 0) {
                    // We didn't need to encode anything!
                    return s;
                } else {
                    // Presumably, we've already done some encoding.
                    encoded.append(s, current, oldLength);
                    return encoded.toString();
                }
            }

            if (encoded == null) {
                encoded = new StringBuilder();
            }

            if (nextToEncode > current) {
                // Append allowed characters leading up to this point.
                encoded.append(s, current, nextToEncode);
            } else {
                // assert nextToEncode == current
            }

            // Switch to "encoding" mode.

            // Find the next allowed character.
            current = nextToEncode;
            int nextAllowed = current + 1;
            while (nextAllowed < oldLength
                    && !isAllowed(s.charAt(nextAllowed), allow)) {
                nextAllowed++;
            }

            // Convert the substring to bytes and encode the bytes as
            // '%'-escaped octets.
            String toEncode = s.substring(current, nextAllowed);
            try {
                byte[] bytes = toEncode.getBytes(DEFAULT_ENCODING);
                int bytesLength = bytes.length;
                for (int i = 0; i < bytesLength; i++) {
                    encoded.append('%');
                    encoded.append(HEX_DIGITS[(bytes[i] & 0xf0) >> 4]);
                    encoded.append(HEX_DIGITS[bytes[i] & 0xf]);
                }
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }

            current = nextAllowed;
        }

        // Encoded could still be null at this point if s is empty.
        return encoded == null ? s : encoded.toString();
    }

    /**
     * Returns true if the given character is allowed.
     *
     * @param c character to check
     * @param allow characters to allow
     * @return true if the character is allowed or false if it should be
     *  encoded
     */
    private static boolean isAllowed(char c, String allow) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')
                || "_-!.~'()*".indexOf(c) != NOT_FOUND
                || (allow != null && allow.indexOf(c) != NOT_FOUND);
    }
}