package com.hunofox.baseFramework.downLoad

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hunofox.baseFramework.utils.CheckUtils

/**
 * 项目名称：libproject
 * 项目作者：胡玉君
 * 创建日期：2019/12/2 17:03.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class DownLoadReceiver : BroadcastReceiver() {

    private var listener: DownLoadHelper.DownLoadListener? = null
    private var downId: Long = 0
    fun setDownLoadListener(listener: DownLoadHelper.DownLoadListener?, downId: Long) {
        this.listener = listener
        this.downId = downId
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (manager.getUriForDownloadedFile(downId) != null && this.downId == downId) {
                val query = DownloadManager.Query()
                val cursor = manager.query(query.setFilterById(downId))
                if (cursor != null && cursor.moveToNext()) {
                    val filePath =
                        Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).path
                    if (!CheckUtils.isEmpty(filePath)) {
                        listener?.onSuccess(filePath!!)
                    } else {
                        listener?.onFailed("下载失败,没有匹配到对应的下载文件(001)")
                    }
                    cursor.close()
                }
            } else {
                listener?.onFailed("下载失败,没有匹配到对应的下载文件(002)")
            }
        }
    }

}