package com.yandex.disk.rest.json;

public class HttpResponse {

    int httpCode;
    String httpMessage;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpCode=" + httpCode +
                ", httpMessage='" + httpMessage + '\'' +
                '}';
    }
}