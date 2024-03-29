package com.hunofox.baseFramework.okHttp.builder;

import com.hunofox.baseFramework.okHttp.request.PostFileRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import okhttp3.MediaType;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFileBuilder extends BaseBuilder {

    private File file;
    private MediaType mediaType;

    public BaseBuilder file(File file) {
        this.file = file;
        return this;
    }

    public BaseBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }


    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType, isRetry, readTimeOut, writeTimeOut, connTimeOut).build();
    }

    @Override
    public PostFileBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostFileBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFileBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostFileBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PostFileBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostFileBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<String, String>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public PostFileBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public PostFileBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }
}
