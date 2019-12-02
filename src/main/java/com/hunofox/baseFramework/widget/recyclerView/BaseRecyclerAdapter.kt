package com.hunofox.baseFramework.widget.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 项目名称：RecyclerView适配器基类
 * 项目作者：胡玉君
 * 创建日期：2019-12-02 14:24.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 为RecyclerView添加header和footer时需注意：
 *
 * 1. 应使用以下方法填充布局，否则不能充满整个布局
 * View footView = LayoutInflater.from(activity).inflate(R.layout.add_bank_card, recyclerView, false);
 *
 *
 * 2. 必须在RecyclerView被初始化完毕后调用：
 * adaptor.addFootView(footView);
 * ----------------------------------------------------------------------------------------------------
 */
abstract class BaseRecyclerAdapter<D, H:RecyclerView.ViewHolder>(val datas:ArrayList<D>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    /** 返回布局文件 */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /** 该方法即onCreateViewHolder，在实例化Adaptor时view已经被创建  */
    protected abstract fun onCreateHolder(view: View, parent: ViewGroup, viewType: Int): H

    /** 该方法即onBindViewHolder方法  */
    protected abstract fun onBindHolder(holder: H, position: Int)

    override fun getItemCount(): Int = datas.size + getHeadersCount() + getFootersCount()

    override fun getItemId(position: Int): Long = position.toLong()

    private var onClickListener:((View, Int) -> Unit)? = null
    fun setOnClickListener(onClickListener:((View, Int) -> Unit)){
        this.onClickListener = onClickListener
    }
    override fun onClick(v: View) {
        onClickListener?.invoke(v, v.tag as Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            headerViews.get(viewType) != null -> HeadAndFootHolder(headerViews.get(viewType)!!)
            footViews.get(viewType) != null -> HeadAndFootHolder(footViews.get(viewType)!!)
            else -> onCreateTypeHolder(parent, viewType)
        }
    }
    protected fun onCreateTypeHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutId(), parent, false)
        view.setOnClickListener(this)
        return onCreateHolder(view, parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = position - getHeadersCount()
        if (isHeaderView(position)) {
            return
        }
        if (isFooterView(position)) {
            return
        }
        onBindTypeHolder(holder, position - getHeadersCount())
    }
    protected fun onBindTypeHolder(holder: RecyclerView.ViewHolder, position: Int){
        onBindHolder(holder as H, position)
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderView(position)) {
            return headerViews.keyAt(position)
        } else if (isFooterView(position)) {
            return footViews.keyAt(position - getHeadersCount() - datas.size)
        }
        return getViewType(position - getHeadersCount())
    }
    protected fun getViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    /** 该方法只能在onDestroy()方法中调用，否则会出问题  */
    fun release() {
        datas.clear()
    }

    /**------------------------------------以下为head和foot-------------------------------------------- */

    private val RECYCLER_HEADER = 100000
    private val RECYCLER_FOOTER = 200000

    //HeaderView和footView的集合
    private val headerViews = SparseArrayCompat<View>()
    private val footViews = SparseArrayCompat<View>()

    /** 是否是HeaderView  */
    private fun isHeaderView(position: Int): Boolean {
        return position < getHeadersCount()
    }

    /** Header的数量  */
    private fun getHeadersCount(): Int {
        return headerViews.size()
    }

    /** 是否是FootView  */
    private fun isFooterView(position: Int): Boolean {
        return position >= getHeadersCount() + datas.size && getFootersCount() > 0
    }

    /** Foot的数量  */
    private fun getFootersCount(): Int {
        return footViews.size()
    }

    /** 添加header和footer  */
    fun addHeaderView(view: View) {
        headerViews.put(headerViews.size() + RECYCLER_HEADER, view)
        notifyDataSetChanged()
    }

    fun addFootView(view: View) {
        footViews.put(footViews.size() + RECYCLER_FOOTER, view)
        notifyDataSetChanged()
    }

    /** 删除header和footer  */
    fun remonveHeaderView(position: Int) {
        if (isHeaderView(position)) {
            headerViews.remove(position)
        }
    }

    fun removeFootView(position: Int) {
        if (isFooterView(position)) {
            footViews.remove(position)
        }
    }

    private inner class HeadAndFootHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /** 重写该方法以使每个header和footer独立占据一行  */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager as GridLayoutManager?
            val spanSizeLookup = gridLayoutManager!!.spanSizeLookup

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    if (headerViews.get(viewType) != null)
                        return gridLayoutManager.spanCount
                    else if (footViews.get(viewType) != null)
                        return gridLayoutManager.spanCount
                    else if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position)
                    return 1
                }
            }
            gridLayoutManager.spanCount = gridLayoutManager.spanCount
        }
    }
}