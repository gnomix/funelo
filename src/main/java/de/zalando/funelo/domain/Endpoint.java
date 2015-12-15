package de.zalando.funelo.domain;

import io.vertx.core.http.HttpMethod;

public class Endpoint {

    private String topic;
    private String path;
    private HttpMethod method;
    private Format format = Format.JSON;
    private Compression compression = Compression.UNCOMPRESSED;

    public Endpoint() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Format getFormat() {
        return format;
    }

    public Compression getCompression() {
        return compression;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "path='" + path + '\'' +
                ", method=" + method +
                ", format=" + format +
                ", compression=" + compression +
                '}';
    }
}
