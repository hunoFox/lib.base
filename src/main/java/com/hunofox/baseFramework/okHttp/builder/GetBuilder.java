package com.hunofox.baseFramework.okHttp.builder;


import com.hunofox.baseFramework.okHttp.request.GetRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class GetBuilder extends BaseBuilder {
    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }
        Logger.d("GET请求报文", url + "");
        return new GetRequest(url, tag, params, headers, isRetry, readTimeOut, writeTimeOut, connTimeOut).build();
    }

    private String appendParams(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public GetBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public GetBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public GetBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public GetBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public GetBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public GetBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public GetBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }
}
