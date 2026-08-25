package dev.aftermath.sdk.model;

import java.util.Map;

public class RequestSnapshot {
    private String method;
    private String uri;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;
    private long timestamp;

    public RequestSnapshot() {
    }

    public RequestSnapshot(String method, String uri, Map<String, String> queryParams, Map<String, String> headers, String body, long timestamp) {
        this.method = method;
        this.uri = uri;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
