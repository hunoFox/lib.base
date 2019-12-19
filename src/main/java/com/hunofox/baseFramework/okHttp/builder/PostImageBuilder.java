package com.hunofox.baseFramework.okHttp.builder;

import android.util.Log;
import com.hunofox.baseFramework.okHttp.request.PostImageRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 创建：sunqiujing
 * 日期: on 16/5/25 09:28
 */
public class PostImageBuilder extends BaseBuilder {

    private List<FileInput> images = new ArrayList<FileInput>();

    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }
        Logger.d("build", url + "(PostImageBuilder.java:28)");
        return new PostImageRequest(url, tag, null, headers, images, isRetry, readTimeOut, writeTimeOut, connTimeOut).build();
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

    public PostImageBuilder addImage(String name, String filename, File file) {
        images.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

    //
    @Override
    public PostImageBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostImageBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostImageBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostImageBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PostImageBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }


    @Override
    public PostImageBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<String, String>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public PostImageBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public PostImageBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }
}
