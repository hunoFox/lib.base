package com.hunofox.baseFramework.okHttp;

import java.util.HashMap;

/**
 * 项目名称：HttpRequest
 * 项目作者：胡玉君
 * 创建日期：2017/7/14 9:30.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class RequestParams extends HashMap<String, Object> {

    public RequestParams() {}

    public RequestParams(String key, Object value) {
        this.put(key, value);
    }
}
