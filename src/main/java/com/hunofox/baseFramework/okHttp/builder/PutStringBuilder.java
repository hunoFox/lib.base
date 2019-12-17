package com.hunofox.baseFramework.okHttp.builder;


import com.hunofox.baseFramework.okHttp.request.PutRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;
import okhttp3.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * *********************
 * PutStringBuilder.java
 * package com.zhy.http.okhttp.builder;
 * com.zhy.http.okhttp.builder
 * <p>
 * sunqiujing
 * 2016-4-28
 * public class PutStringBuilder{ }
 * PutStringBuilder
 * ************************
 */
public class PutStringBuilder extends BaseBuilder {
    private String content;
    private MediaType mediaType;


    public PutStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PutStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }


    @Override
    public RequestCall build() {
        Logger.d("PUT请求报文", url + ":" + content + "");
        return new PutRequest(url, tag, params, headers, content, mediaType, isRetry, readTimeOut, writeTimeOut,connTimeOut).build();
    }

    @Override
    public PutStringBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PutStringBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PutStringBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PutStringBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PutStringBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PutStringBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public PutStringBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public PutStringBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }
}