package com.hunofox.baseFramework.okHttp.builder;

import com.hunofox.baseFramework.okHttp.request.DeleteStringRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;
import okhttp3.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 功能：
 * 创建：sunqiujing
 * 日期: on 16/5/13 09:36
 */
public class DeleteBuilder extends BaseBuilder {

    private String content;
    private MediaType mediaType;


    public DeleteBuilder content(String content) {
        this.content = content;
        return this;
    }

    public DeleteBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public DeleteBuilder url(String url) {
        this.url = url;
        return this;
    }

    public DeleteBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public DeleteBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public DeleteBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public DeleteBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public DeleteBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<String, String>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public DeleteBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public DeleteBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }

    @Override
    public RequestCall build() {
        Logger.d("DEL请求报文", url + "");
        return new DeleteStringRequest(url, tag, params, headers, content, mediaType, isRetry, readTimeOut, writeTimeOut, connTimeOut).build();
    }


}
