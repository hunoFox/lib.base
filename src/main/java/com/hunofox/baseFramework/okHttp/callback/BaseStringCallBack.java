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
        StringBuilder sb = new StringBuilder();
        sb.append("ResponseCode：" + responseCode + "\n");
        sb.append("flag：" + flag + "\n");
        sb.append(e.getMessage() + "\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append(writer.toString());

        Logger.e(flag + "响应(" + responseCode + ")", sb.toString() + "(BaseStringCallBack.java:49)");
        onResponseError(call, responseCode, e, flag);
    }

    @Override
    public void onResponse(String response, String flag) {
        Logger.d(flag + "响应(200)", response + "(BaseStringCallBack.java:55)");
        onResponseSuccess(response, flag);
    }

    protected abstract void onResponseSuccess(String json, String flag);

    protected abstract void onResponseError(Call call, int responseCode, Exception e, String flag);


}
