package com.hunofox.baseFramework

import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 项目名称：LibProject
 * 项目作者：胡玉君
 * 创建日期：2019-12-02 14:24.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class BaseRecylcerAdaptor<D, H:RecyclerView.ViewHolder>(val datas:ArrayList<D>) : RecyclerView.Adapter<H>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}