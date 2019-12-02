package com.hunofox.baseFramework.widget.recyclerView;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 项目名称：可以禁用上下滑动的Manager
 * 项目作者：胡玉君
 * 创建日期：2016/4/8 10:38.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class RecyclerLayoutManager extends LinearLayoutManager {

    public RecyclerLayoutManager(Context context) {
        super(context);
    }

    /** 上下滑动开关 */
    private boolean isScroller = true;
    public void isScrollerEnable(boolean flag){
        this.isScroller = flag;
    }
    @Override
    public boolean canScrollVertically() {
        return isScroller && super.canScrollVertically();
    }
}
