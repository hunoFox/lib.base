package com.hunofox.baseFramework.widget.recyclerView

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hunofox.baseFramework.R


/**
 * 项目名称：上啦加载更多脚部
 * 项目作者：胡玉君
 * 创建日期：2017/8/29 15:31.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class LoadMoreFooter : RelativeLayout {

    private var mState = State.Normal
    private var mLoadingView: View? = null
    private var mNetworkErrorView: View? = null
    private var mTheEndView: View? = null
    private var progressBar: ProgressBar? = null
    private var mLoadingTextView: TextView? = null
    private var mNoMoreTextView: TextView? = null
    private var mNoNetWorkTextView: TextView? = null
    private var loadingHint: String? = null
    private var noMoreHint: String? = null
    private var noNetWorkHint: String? = null

    private var viewBg: LinearLayout? = null

    var state: Int
        get() = mState
        set(status) = setState(status, true)

    val footView: View
        get() = this

    val footerChildView: View
        get() = this.getChildAt(0)

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        View.inflate(context, R.layout.widget_load_more_view_sub, this)
        viewBg = findViewById(R.id.view_bg)
        setOnClickListener(null)

        //初始为隐藏状态
        onReset()
    }

    fun setLoadingHint(hint: String) {
        this.loadingHint = hint
        if (mLoadingTextView != null) {
            mLoadingTextView!!.text = hint
        }
    }

    fun setNoMoreHint(hint: String) {
        this.noMoreHint = hint
        if (mNoMoreTextView != null) {
            mNoMoreTextView!!.text = hint
        }
    }

    fun setNoNetWorkHint(hint: String) {
        this.noNetWorkHint = hint
        if (mNoNetWorkTextView != null) {
            mNoNetWorkTextView!!.text = hint
        }
    }

    fun setViewBackgroundColor(color: Int) {
        viewBg!!.setBackgroundColor(ContextCompat.getColor(context, color))
        this.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun onReset() {
        onComplete()
    }

    fun onLoading() {
        state = State.Loading
    }

    fun onComplete() {
        state = State.Normal
    }

    fun onNoMore() {
        state = State.NoMore
    }

    /**
     * 设置状态
     *
     * @param status
     * @param showView 是否展示当前View
     */
    fun setState(status: Int, showView: Boolean) {
        if (mState == status) {
            return
        }
        mState = status

        when (status) {

            State.Normal -> {
                setOnClickListener(null)
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }

                if (mTheEndView != null) {
                    mTheEndView!!.visibility = View.GONE
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView!!.visibility = View.GONE
                }
            }
            State.Loading -> {
                setOnClickListener(null)
                if (mTheEndView != null) {
                    mTheEndView!!.visibility = View.GONE
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView!!.visibility = View.GONE
                }

                if (mLoadingView == null) {
                    val viewStub = findViewById<View>(R.id.loading) as ViewStub
                    mLoadingView = viewStub.inflate()

                    progressBar = mLoadingView!!.findViewById<View>(R.id.progressBar) as ProgressBar
                    mLoadingTextView = mLoadingView!!.findViewById<View>(R.id.tv_loading) as TextView
                }

                mLoadingView!!.visibility = if (showView) View.VISIBLE else View.GONE
                progressBar!!.visibility = View.VISIBLE
                mLoadingTextView!!.text = if (TextUtils.isEmpty(loadingHint)) "正在加载更多数据" else loadingHint
            }
            State.NoMore -> {
                setOnClickListener(null)
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView!!.visibility = View.GONE
                }

                if (mTheEndView == null) {
                    val viewStub = findViewById<View>(R.id.end) as ViewStub
                    mTheEndView = viewStub.inflate()

                    mNoMoreTextView = mTheEndView!!.findViewById<View>(R.id.tv_end) as TextView
                } else {
                    mTheEndView!!.visibility = View.VISIBLE
                }

                mTheEndView!!.visibility = if (showView) View.VISIBLE else View.GONE
                mNoMoreTextView!!.text = if (TextUtils.isEmpty(noMoreHint)) "已全部加载完毕" else noMoreHint
            }
            State.NetWorkError -> {
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }

                if (mTheEndView != null) {
                    mTheEndView!!.visibility = View.GONE
                }

                if (mNetworkErrorView == null) {
                    val viewStub = findViewById<View>(R.id.network_error) as ViewStub
                    mNetworkErrorView = viewStub.inflate()
                    mNoNetWorkTextView = mNetworkErrorView!!.findViewById<View>(R.id.tv_error) as TextView
                } else {
                    mNetworkErrorView!!.visibility = View.VISIBLE
                }

                mNetworkErrorView!!.visibility = if (showView) View.VISIBLE else View.GONE
                mNoNetWorkTextView!!.text = if (TextUtils.isEmpty(noNetWorkHint)) "网络环境异常" else noNetWorkHint
            }
        }
    }

    class State {

        companion object{
            @JvmStatic
            val Normal = 0
            /**正常 */

            @JvmStatic
            val NoMore = 2
            /**加载到最底了 */

            @JvmStatic
            val Loading = 1
            /**加载中.. */

            @JvmStatic
            val NetWorkError = -1
            /**网络异常 */
        }

    }


}
