package com.yandex.disk.rest.retrofit;

import com.google.gson.Gson;
import com.yandex.disk.rest.Log;
import com.yandex.disk.rest.exceptions.UserUnauthorizedException;
import com.yandex.disk.rest.exceptions.WebdavIOException;
import com.yandex.disk.rest.json.ApiError;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ErrorHandlerImpl implements ErrorHandler {

    private static final String TAG = "ErrorHandlerImpl";

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        try {
            RetrofitError.Kind kind = retrofitError.getKind();
            switch (kind) {
                case NETWORK:
                    return new WebdavIOException(retrofitError.getMessage());

                case CONVERSION:    // TODO XXX test it
                    return new WebdavIOException(retrofitError.getCause());

                case HTTP:
                    Response response = retrofitError.getResponse();
                    int httpCode = response.getStatus();
                    Log.d(TAG, "getStatus=" + httpCode);
                    Reader reader = new InputStreamReader(response.getBody().in());
                    ApiError apiError = new Gson().fromJson(reader, ApiError.class);
                    switch (httpCode) {
                        case 401:
                            return new UserUnauthorizedException("http code "+httpCode);

                        // TODO XXX other 4xx codes

                        default:
                            return new WebdavIOException(apiError != null
                                    ? apiError.getDescription()
                                    : "HTTP Error code " + retrofitError.getResponse().getStatus());
                    }

                case UNEXPECTED:    // TODO XXX use other exception
                    return new WebdavIOException(retrofitError.getCause());

                default:
                    return new WebdavIOException("ErrorHandler: unhandled error " + kind.name());
            }
        } catch (IOException ex) {
            Log.d(TAG, "errorHandler", retrofitError);
            return new WebdavIOException(ex);
        }
    }
}
