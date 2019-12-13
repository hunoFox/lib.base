package com.hunofox.baseFramework.okHttp;

import com.hunofox.baseFramework.okHttp.builder.*;

/**
 * 项目名称：HttpRequest
 * 项目作者：胡玉君
 * 创建日期：2017/7/13 17:26.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class OkHttp {

    public static final long DEFAULT_MILLISECONDS = 15000;//默认超时时间15000ms

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder post() {
        return new PostStringBuilder();
    }

    public static PutStringBuilder put() {
        return new PutStringBuilder();
    }

    public static DeleteBuilder delete() {
        return new DeleteBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder postForm() {
        return new PostFormBuilder();
    }

    public static PostImageBuilder postImage() {
        return new PostImageBuilder();
    }
}
