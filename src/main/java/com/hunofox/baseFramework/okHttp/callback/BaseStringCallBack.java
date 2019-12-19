package com.hunofox.baseFramework.okHttp.callback;

import com.hunofox.baseFramework.utils.Logger;
import okhttp3.Call;
import okhttp3.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 项目名称：HttpRequest
 * 项目作者：胡玉君
 * 创建日期：2017/7/13 17:18.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 响应返回数据的一种实现
 *
 * 目前服务端大多以json字符串的形式返回
 * ----------------------------------------------------------------------------------------------------
 */
public abstract class BaseStringCallBack extends BaseCallback<String>{

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }

    @Override
    public void onError(Call call, int responseCode, Exception e, String flag) {
        Logger.e(flag + "错误响应(" + responseCode + ")", e.getMessage() + "(BaseStringCallBack.java:49)");
        e.printStackTrace();

        onResponseError(call, responseCode, e, flag);
    }

    @Override
    public void onResponse(String response, String flag) {
        Logger.d(flag + "成功响应(200)", response + "(BaseStringCallBack.java:55)");
        onResponseSuccess(response, flag);
    }

    protected abstract void onResponseSuccess(String json, String flag);

    protected abstract void onResponseError(Call call, int responseCode, Exception e, String flag);


}
