package de.zalando.funelo.domain;

import java.util.Map.Entry;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RequestData {
    @JsonSerialize(as = Iterable.class)
    private Iterable<Entry<String, String>> headers;
    @JsonSerialize(as = Iterable.class)
    private Iterable<Entry<String, String>> params;

    private String uri;
    private String path;

    public RequestData() { }

    public RequestData(final Iterable<Entry<String, String>> headers, final Iterable<Entry<String, String>> params,
            final String uri, final String path) {
        super();
        this.headers = headers;
        this.params = params;
        this.uri = uri;
        this.path = path;
    }

    public Iterable<Entry<String, String>> getHeaders() {
        return headers;
    }

    public void setHeaders(final Iterable<Entry<String, String>> headers) {
        this.headers = headers;
    }

    public Iterable<Entry<String, String>> getParams() {
        return params;
    }

    public void setParams(final Iterable<Entry<String, String>> params) {
        this.params = params;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

}
