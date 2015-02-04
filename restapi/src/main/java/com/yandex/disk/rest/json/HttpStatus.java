package com.yandex.disk.rest.json;

public class HttpStatus {

    public enum Result {
        done, inProgress, error
    }

    public Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "HttpStatus.result=" + result;
    }
}
