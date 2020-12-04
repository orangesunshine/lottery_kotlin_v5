package com.bdb.lottery.datasource.cocos

import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener

open class FileDownloadListenerAdapter : FileDownloadListener() {
    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun completed(task: BaseDownloadTask?) {

    }

    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun error(task: BaseDownloadTask?, e: Throwable?) {

    }

    override fun warn(task: BaseDownloadTask?) {

    }

}