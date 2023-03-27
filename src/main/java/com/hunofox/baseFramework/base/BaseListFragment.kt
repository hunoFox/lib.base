package com.hunofox.baseFramework.base

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hunofox.baseFramework.R
import com.hunofox.baseFramework.databinding.FragmentBaseListBinding
import com.hunofox.baseFramework.widget.recyclerView.BaseRecyclerAdapter
import com.hunofox.baseFramework.widget.recyclerView.LoadMoreRecyclerView

/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/29 15:06.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * abstract抽象类时，open关键字可以被忽略
 *
 * 绿色注释部分均可以被调用
 * ----------------------------------------------------------------------------------------------------
 */
abstract class BaseListFragment : BaseFragment() , SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadMoreListener{

    /**
     * 子类必须实现的方法
     *
     * 返回一个BaseRecyclerAdaptor实例
     */
    abstract fun getAdaptor(): BaseRecyclerAdapter<*, out RecyclerView.ViewHolder>?

    /**
     * 设置列数
     *
     * @param column 列数
     */
    fun setColumnCount(column:Int){
        this.columnCount = column
    }
    fun getColumnCount():Int = columnCount

    /**
     * 子类重写该方法处理正在刷新时的事件
     *
     * 注意：super事件不能删除
     */
    override fun onRefresh() {
        bing.swipeRefreshLayout.isRefreshing = true
        bing.recyclerView.setLoadMoreEnable(false)
    }

    /**
     * 子类重写该方法处理正在上拉加载的事件
     *
     * 注意：super事件不能删除
     */
    override fun onLoadMore() {
        bing.swipeRefreshLayout.isRefreshing = false
        bing.swipeRefreshLayout.isEnabled = false
    }

    /**
     * 设置下拉刷新是否启用
     *
     * @param enable true为启用
     */
    fun setRefreshEnable(enable:Boolean){
        this.refreshEnable = enable
        bing.swipeRefreshLayout.isEnabled = enable
    }

    /**
     * 设置上拉加载是否启用
     *
     * @param enable true为启用
     */
    fun setLoadMoreEnable(enable:Boolean){
        this.loadMoreEnable = enable
        bing.recyclerView.setLoadMoreEnable(enable)
    }

    /**
     * 设置是否展示正在刷新按钮
     *
     * @param refresh true为正在刷新,false为停止刷新
     */
    fun setRefreshing(refresh:Boolean){
        if(refresh){
            onRefresh()
        }else{
            bing.swipeRefreshLayout.isRefreshing = false
            bing.recyclerView.setLoadMoreEnable(loadMoreEnable)
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param state 取值如下：
     *
     *      STATE_COMPLETE_IDLE     加载完成
     *      STATE_LOADING_DATA      加载中
     *      STATE_NO_MORE           没有更多数据，不再加载
     */
    fun setLoadMoreState(state:Int){
        bing.recyclerView?.loadMoreState = state
        bing.swipeRefreshLayout?.isEnabled = refreshEnable
        if(state == LoadMoreRecyclerView.State.STATE_LOADING_DATA){
            bing.swipeRefreshLayout?.isEnabled = false
        }
    }

    private var refreshEnable = true
    private var loadMoreEnable = true
    private var columnCount = 1

    private var _binding:FragmentBaseListBinding? = null
    private val bing get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBaseListBinding.inflate(inflater,container,false)
        return bing.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初始化下拉刷新
        bing.swipeRefreshLayout.setOnRefreshListener(this)
        bing.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_light,
                R.color.colorPrimaryDark,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light)
        bing.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white)
        bing.swipeRefreshLayout.setProgressViewOffset(false, 0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics).toInt())
        bing.swipeRefreshLayout.isEnabled = refreshEnable

        //初始化RecyclerView及上拉加载
        bing.recyclerView.isFocusable = false
        bing.recyclerView.setOnLoadMoreListener(this)
        bing.recyclerView.setColumnCount(columnCount)
        bing.recyclerView.adapter = getAdaptor()
        bing.recyclerView.setLoadMoreEnable(loadMoreEnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        getAdaptor()?.release()
        super.onDestroy()
    }

    override fun onErr(retFlag: String, retMsg: String, response: String?, flag: String?) {
        super.onErr(retFlag, retMsg, response, flag)
        setRefreshing(false)
        setLoadMoreState(LoadMoreRecyclerView.State.STATE_COMPLETE_IDLE)
    }
}