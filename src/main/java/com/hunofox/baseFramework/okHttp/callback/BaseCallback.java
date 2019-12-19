package com.hunofox.baseFramework.okHttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseCallback<T> {

    public void onBefore(Request request, String flag) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter(String flag) {

    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, String flag) {

    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    public abstract void onError(Call call, int responseCode, Exception e, String flag);

    public abstract void onResponse(T response, String flag);

}