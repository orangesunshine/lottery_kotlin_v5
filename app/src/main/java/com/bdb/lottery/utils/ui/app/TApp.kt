package com.bdb.lottery.utils.ui.app

import android.content.Context
import com.bdb.lottery.extension.equalsNSpace
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TApp @Inject constructor(@ApplicationContext val context: Context) {

    /**
     * 判断是不是当前process
     */
    fun isMainProcess(): Boolean {
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = Apps.getCurrentProcessName()
        return processName.equalsNSpace(packageName)
    }

    /**
     * 判断利博会平台
     */
    fun isLBH(): Boolean {
        return Apps.isPlatform("libohui")
    }
}