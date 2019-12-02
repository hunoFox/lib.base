package com.hunofox.baseFramework.downLoad

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.hunofox.baseFramework.R
import com.hunofox.baseFramework.utils.NumberUtils

/**
 * 项目名称：libproject
 * 项目作者：胡玉君
 * 创建日期：2019/12/2 16:20.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class DownLoadDialog: DialogFragment() {

    val FINISHED = -100.00
    val CONCULATE = -1.0
    val ERROR = -2.00

    private var tvProgress: TextView? = null
    private var tvSize: TextView? = null
    private var progressBar: ProgressBar? = null

    private var maxSize = CONCULATE

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.Dialog)
        val view = View.inflate(activity, R.layout.dialog_down_load, null)
        progressBar = view.findViewById(R.id.progressBar) as ProgressBar
        tvProgress = view.findViewById(R.id.tv_progress) as TextView
        tvSize = view.findViewById(R.id.tv_size) as TextView
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun setCurrentAndMaxSize(currentSize: Double, maxSize: Double) {
        if (maxSize != CONCULATE && maxSize != FINISHED && maxSize != ERROR) {
            this.maxSize = maxSize
        }

        val current =
            if (currentSize / 1024 >= 1024) NumberUtils.formatDoubleDecimal((currentSize / (1024 * 1024)).toString()) + "M" else NumberUtils.formatDoubleDecimal(
                (currentSize / 1024).toString()
            ) + "KB"
        val max =
            if (this.maxSize / 1024 >= 1024) NumberUtils.formatDoubleDecimal((this.maxSize / (1024 * 1024)).toString()) + "M" else NumberUtils.formatDoubleDecimal(
                (this.maxSize / 1024).toString()
            ) + "KB"
        if (tvSize != null && tvProgress != null && progressBar != null) {
            if (maxSize == CONCULATE) {
                //下载开始
                progressBar!!.progress = 0
                tvProgress!!.text = "下载进度：0%"
                tvSize!!.text = "(已下载0KB / 总大小正在计算)"
            } else if (maxSize == FINISHED) {
                //下载完成
                progressBar!!.progress = 100
                tvProgress!!.text = "下载进度：100%"
                tvSize!!.text = "(已下载$max / 总大小$max)"
            } else if (maxSize == ERROR) {
                //下载出错，停留在当前位置，不做任何处理

            } else {
                //正常下载
                progressBar!!.progress = (currentSize / maxSize * 100).toInt()
                tvProgress!!.text =
                    "下载进度：" + NumberUtils.formatDoubleDecimal((currentSize / maxSize * 100).toString()) + "%"
                tvSize!!.text = "(已下载$current / 总大小$max)"
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        setCurrentAndMaxSize(0.0, CONCULATE)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        val show = super.show(transaction, tag)
        setCurrentAndMaxSize(0.0, CONCULATE)
        return show
    }

    override fun onCancel(dialog: DialogInterface) {
        if (cancelListener != null) {
            cancelListener!!.onCancel(dialog)
        }
        super.onCancel(dialog)
    }

    private var cancelListener: DialogInterface.OnCancelListener? = null
    fun setOnCancelListener(listener: DialogInterface.OnCancelListener) {
        this.cancelListener = listener
    }


}