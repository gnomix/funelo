package de.zalando.funelo.domain;

import io.vertx.core.http.HttpMethod;

public class Endpoint {

    private String path;
    private HttpMethod method;
    private String format;

    public Endpoint() {}

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getFormat() {
        return format;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "path='" + path + '\'' +
                ", method=" + method +
                ", format='" + format + '\'' +
                '}';
    }
}
