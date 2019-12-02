package com.hunofox.baseFramework.okHttp.request;


import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * *********************
 * PutRequest.java
 * package com.zhy.http.okhttp.request;
 * com.zhy.http.okhttp.request
 * <p>
 * sunqiujing
 * 2016-4-28
 * public class PutRequest{ }
 * PutRequest
 * ************************
 */
public class PutRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("application/json;charset=utf-8");

    private String content;
    private MediaType mediaType;


    public PutRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, String content, MediaType mediaType, boolean isRetry, long timeOut) {
        super(url, tag, params, headers, isRetry, timeOut);
        this.content = content;
        this.mediaType = mediaType;

        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_PLAIN;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.put(requestBody).build();
    }

    @Override
    public String toString() {
        return super.toString() + ", requestBody{content=" + content + "} ";
    }
}
