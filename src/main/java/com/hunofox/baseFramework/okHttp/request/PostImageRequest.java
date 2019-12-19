package com.hunofox.baseFramework.okHttp.request;


import com.hunofox.baseFramework.okHttp.builder.PostImageBuilder;
import com.hunofox.baseFramework.okHttp.callback.BaseCallback;
import com.hunofox.baseFramework.okHttp.utils.OkHttpUtils;
import okhttp3.*;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 创建：sunqiujing
 * 日期: on 16/5/25 09:34
 */
public class PostImageRequest extends OkHttpRequest {

    private List<PostImageBuilder.FileInput> imageList;

    public PostImageRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, List<PostImageBuilder.FileInput> files, boolean isRetry, long readTimeOut, long writeTimeOut, long connTimeOut) {
        super(url, tag, params, headers, isRetry, readTimeOut, writeTimeOut, connTimeOut);
        this.imageList = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (imageList == null || imageList.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder);

            for (int i = 0; i < imageList.size(); i++) {
                PostImageBuilder.FileInput fileInput = imageList.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final BaseCallback callback, final String flag) {
        if (callback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten * 1.0f / contentLength, flag);
                    }
                });
            }
        });
        return countingRequestBody;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private void addParams(FormBody.Builder builder) {
        if (params == null || params.isEmpty()) {
            builder.add("1", "1");
            return;
        }

        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (imageList != null) {
            for (PostImageBuilder.FileInput file : imageList) {
                sb.append(file.toString() + "  ");
            }
        }
        return sb.toString();
    }

}
