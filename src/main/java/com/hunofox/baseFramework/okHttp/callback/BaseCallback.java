package com.hunofox.baseFramework.okHttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseCallback<T> {
    /**
     * UI Thread
     *
     * @param request
     */

    public String className_;
    public Object beanName_;

    public void setResultBeanName(Object beanName) {
        this.beanName_ = beanName;
    }

    public void setResultClassName(String className) {
        this.className_ = className;
    }

    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() {

    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress) {}

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    public abstract void onError(Call call, int responseCode, Exception e, String flag);

    public abstract void onResponse(T response, String flag);

}