package com.hunofox.baseFramework.okHttp.request;

import com.hunofox.baseFramework.okHttp.OkHttp;
import com.hunofox.baseFramework.okHttp.callback.BaseCallback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * Created by zhy on 15/11/6.
 */
public abstract class OkHttpRequest {

    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    protected boolean isRetry;
    protected long timeOut = OkHttp.DEFAULT_MILLISECONDS;

    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag,
                            Map<String, String> params, Map<String, String> headers,
                            boolean isRetry,
                            long timeOut) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.isRetry = isRetry;
        this.timeOut = timeOut;
    }


    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final BaseCallback callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(Request.Builder builder, RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this, isRetry, timeOut);
    }

    public Request generateRequest(BaseCallback callback) {
        RequestBody requestBody = wrapRequestBody(buildRequestBody(), callback);
        prepareBuilder();
        return buildRequest(builder, requestBody);
    }


    private void prepareBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            if(headers.get(key) != null){
                headerBuilder.add(key, headers.get(key));
            }
        }
        builder.headers(headerBuilder.build());
    }
}
