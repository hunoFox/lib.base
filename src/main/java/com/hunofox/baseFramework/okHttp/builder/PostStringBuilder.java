package com.hunofox.baseFramework.okHttp.builder;


import com.hunofox.baseFramework.okHttp.request.PostStringRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;
import okhttp3.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostStringBuilder extends BaseBuilder {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        Logger.d("POST请求报文", url + ":" + content + "");
        return new PostStringRequest(url, tag, params, headers, content, mediaType, isRetry, timeOut).build();
    }

    @Override
    public PostStringBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostStringBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostStringBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostStringBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PostStringBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostStringBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public PostStringBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public PostStringBuilder timeOut(long timeOut) {
        this.timeOut = timeOut;
        return this;
    }
}
