/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.rest.retrofit;

import com.google.gson.Gson;
import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.RetrofitConversionException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.http.BadGatewayException;
import com.yandex.disk.rest.exceptions.http.BadRequestException;
import com.yandex.disk.rest.exceptions.http.ConflictException;
import com.yandex.disk.rest.exceptions.http.ForbiddenException;
import com.yandex.disk.rest.exceptions.http.GoneException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.exceptions.http.InsufficientStorageException;
import com.yandex.disk.rest.exceptions.http.InternalServerException;
import com.yandex.disk.rest.exceptions.http.LockedException;
import com.yandex.disk.rest.exceptions.http.MethodNotAllowedException;
import com.yandex.disk.rest.exceptions.http.NotAcceptableException;
import com.yandex.disk.rest.exceptions.http.NotFoundException;
import com.yandex.disk.rest.exceptions.http.NotImplementedException;
import com.yandex.disk.rest.exceptions.http.PreconditionFailedException;
import com.yandex.disk.rest.exceptions.http.ServiceUnavailableException;
import com.yandex.disk.rest.exceptions.http.TooManyRequestsException;
import com.yandex.disk.rest.exceptions.http.UnauthorizedException;
import com.yandex.disk.rest.exceptions.http.UnprocessableEntityException;
import com.yandex.disk.rest.exceptions.http.UnsupportedMediaTypeException;
import com.yandex.disk.rest.json.ApiError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ErrorHandlerImpl implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerImpl.class);

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        RetrofitError.Kind kind = retrofitError.getKind();
        switch (kind) {
            case NETWORK:
                return new NetworkIOException(retrofitError.getCause());

            case CONVERSION:
                return new RetrofitConversionException(retrofitError.getCause());

            case HTTP:
                Response response = retrofitError.getResponse();
                int httpCode = response.getStatus();
                logger.debug("getStatus=" + httpCode);
                Reader reader;
                try {
                    reader = new InputStreamReader(response.getBody().in());
                } catch (IOException ex) {
                    logger.debug("errorHandler", retrofitError);
                    return new NetworkIOException(ex);
                }
                ApiError apiError = new Gson().fromJson(reader, ApiError.class);
                switch (httpCode) {
                    case 400:
                        return new BadRequestException(httpCode, apiError);
                    case 401:
                        return new UnauthorizedException(httpCode, apiError);
                    case 403:
                        return new ForbiddenException(httpCode, apiError);
                    case 404:
                        return new NotFoundException(httpCode, apiError);
                    case 405:
                        return new MethodNotAllowedException(httpCode, apiError);
                    case 406:
                        return new NotAcceptableException(httpCode, apiError);
                    case 409:
                        return new ConflictException(httpCode, apiError);
                    case 410:
                        return new GoneException(httpCode, apiError);
                    case 412:
                        return new PreconditionFailedException(httpCode, apiError);
                    case 415:
                        return new UnsupportedMediaTypeException(httpCode, apiError);
                    case 422:
                        return new UnprocessableEntityException(httpCode, apiError);
                    case 423:
                        return new LockedException(httpCode, apiError);
                    case 429:
                        return new TooManyRequestsException(httpCode, apiError);    // TODO process Retry-After header
                    case 500:
                        return new InternalServerException(httpCode, apiError);
                    case 501:
                        return new NotImplementedException(httpCode, apiError);
                    case 502:
                        return new BadGatewayException(httpCode, apiError);
                    case 503:
                        return new ServiceUnavailableException(httpCode, apiError);
                    case 507:
                        return new InsufficientStorageException(httpCode, apiError);
                    default:
                        return new HttpCodeException(httpCode, apiError);
                }

            case UNEXPECTED:
                return new ServerIOException(retrofitError.getCause());

            default:
                return new ServerIOException("ErrorHandler: unhandled error " + kind.name());
        }
    }
}
