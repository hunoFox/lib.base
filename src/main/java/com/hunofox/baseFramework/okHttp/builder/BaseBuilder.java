package com.hunofox.baseFramework.okHttp.builder;


import com.hunofox.baseFramework.okHttp.OkHttp;
import com.hunofox.baseFramework.okHttp.request.RequestCall;

import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class BaseBuilder {
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;
    protected boolean isRetry = true;

    protected long readTimeOut = OkHttp.DEFAULT_MILLISECONDS_READ;
    protected long writeTimeOut = OkHttp.DEFAULT_MILLISECONDS_WRITE;
    protected long connTimeOut = OkHttp.DEFAULT_MILLISECONDS_CONNECT;

    public abstract BaseBuilder url(String url);

    public abstract BaseBuilder tag(Object tag);

    public abstract BaseBuilder params(Map<String, String> params);

    public abstract BaseBuilder addParams(String key, String val);

    public abstract BaseBuilder headers(Map<String, String> headers);

    public abstract BaseBuilder addHeader(String key, String val);

    public abstract BaseBuilder isRetry(boolean isRetry);

    public abstract BaseBuilder timeOut(long readTimeOut, long writeTimeOut, long connTimeOut);

    public abstract RequestCall build();


}
