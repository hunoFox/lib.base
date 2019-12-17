package com.hunofox.baseFramework.okHttp.builder;


import com.hunofox.baseFramework.okHttp.request.PostFormRequest;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import com.hunofox.baseFramework.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * sqj
 * Created by zhy on 15/12/14.
 */
public class PostFormBuilder extends BaseBuilder {

    private List<FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build() {
        Logger.d("POSTFORM请求报文",url + ":" + params);
        return new PostFormRequest(url, tag, params, headers, files, isRetry, readTimeOut, writeTimeOut,connTimeOut).build();
    }


    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
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
    public PostFormBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostFormBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<String, String>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PostFormBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }


    @Override
    public PostFormBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<String, String>();
        }
        headers.put(key, val);
        return this;
    }

    @Override
    public PostFormBuilder isRetry(boolean isRetry) {
        this.isRetry = isRetry;
        return this;
    }

    @Override
    public PostFormBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut) {
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.connTimeOut = connTimeOut;
        return this;
    }
}
