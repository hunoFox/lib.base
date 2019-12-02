package com.hunofox.baseFramework.okHttp.callback;

import okhttp3.Response;

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

}
