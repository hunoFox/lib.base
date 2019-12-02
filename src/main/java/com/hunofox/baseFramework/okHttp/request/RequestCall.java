package com.hunofox.baseFramework.okHttp.request;


import com.hunofox.baseFramework.okHttp.OkHttp;
import com.hunofox.baseFramework.okHttp.callback.BaseCallback;
import com.hunofox.baseFramework.okHttp.utils.OkHttpUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhy on 15/12/15.
 */
public class RequestCall {

    protected OkHttpRequest okHttpRequest;
    protected Request request;
    protected Call call;
    protected boolean isRetry;
    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connTimeOut;
    protected OkHttpClient clone;

    public RequestCall(OkHttpRequest request, boolean isRetry, long timeOut) {

        readTimeOut(timeOut);
        writeTimeOut(timeOut);
        connTimeOut(timeOut);

        this.okHttpRequest = request;
        this.isRetry = isRetry;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }


    public Call generateCall(BaseCallback callback) {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {

            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttp.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttp.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttp.DEFAULT_MILLISECONDS;

            clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .retryOnConnectionFailure(isRetry)
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = clone.newCall(request);
        } else {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    protected Request generateRequest(BaseCallback callback) {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(BaseCallback callback, String flag) {
        generateCall(callback);

        if (callback != null) {
            callback.onBefore(request);
        }

        OkHttpUtils.getInstance().execute(this, callback, flag);
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() throws IOException {
        generateCall(null);
        return call.execute();
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }


}
