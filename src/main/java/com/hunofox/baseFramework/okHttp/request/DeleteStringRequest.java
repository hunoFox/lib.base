package com.hunofox.baseFramework.okHttp.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * 功能：
 * 创建：sunqiujing
 * 日期: on 16/5/13 10:11
 */
public class DeleteStringRequest extends OkHttpRequest {

    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("application/json;charset=utf-8");

    private String content;
    private MediaType mediaType;

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.delete(requestBody).build();
    }

    public DeleteStringRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, String content, MediaType mediaType, boolean isRetry, long timeOut) {
        super(url, tag, params, headers, isRetry, timeOut);
        this.content = content;
        this.mediaType = mediaType;
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_PLAIN;
        }
    }
}
