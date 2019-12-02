package com.hunofox.baseFramework.okHttp.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private File file;
    private MediaType mediaType;


    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, File file, MediaType mediaType, boolean isRetry, long timeOut) {
        super(url, tag, params, headers, isRetry, timeOut);
        this.file = file;
        this.mediaType = mediaType;
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }

    }

    @Override
    protected RequestBody buildRequestBody() {

        return RequestBody.create(mediaType, file);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {

        return builder.post(requestBody).build();
    }

    @Override
    public String toString() {
        return super.toString() + ", requestBody{uploadfilePath=" + file.getAbsolutePath() + "} ";
    }


}
