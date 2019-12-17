package com.hunofox.baseFramework.okHttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class GetRequest extends OkHttpRequest {

    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, boolean isRetry, long readTimeOut, long writeTimeOut, long connTimeOut) {
        super(url, tag, params, headers, isRetry, readTimeOut,writeTimeOut,connTimeOut);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }
}
