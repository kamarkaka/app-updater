package com.kamarkaka.commons;

import okhttp3.Headers;

public class ResponseData {
    private final int code;
    private final Headers headers;
    private final String body;

    public ResponseData(int code, Headers headers, String body) {
        this.code = code;
        this.headers = headers;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
