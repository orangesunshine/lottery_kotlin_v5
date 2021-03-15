package com.bdb.lottery.utils.file

import android.content.Context
import com.bdb.lottery.utils.resource.Resources
import com.bdb.lottery.utils.sdcard.SDCards
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TPath @Inject constructor(@ApplicationContext private val context: Context) {
    private var cocosDownloadPath: String? = null//cocos下载路径
    private var apkDownloadPath: String? = null//apk下载路径

    //region rootPath下载根路径
    private var rootPath: String? = null//下载根路径
    private fun getRoot(): String? {
        val path = rootPath()
        if (Files.createOrExistsDir(path)) rootPath = path
        return rootPath
    }

    private fun rootPath(): String? {
        var path: String? = null
        if (!Files.isDir(rootPath)) {
            if (SDCards.isSDCardEnableByEnvironment()) path =
                context.getExternalFilesDir("app")?.path
            if (Files.isDir(path)) return path

            val paths = SDCards.getMountedSDCardPath()
            paths?.forEach {
                val pathTmp = it + SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
                if (Files.createOrExistsDir(pathTmp)) {
                    Files.deleteDir(Files.getFileByPath(pathTmp))
                    path = pathTmp
                    return@forEach
                }
            }
        }
        return path
    }
    //endregion

    //region cocos动画下载根路径
    fun cocosDownloadPath(): String? {
        if (Files.createOrExistsDir(cocosDownloadPath)) {
            return cocosDownloadPath
        }
        //根路径处理
        if (!Files.isDir(rootPath)) {
            getRoot()
            if (!Files.isDir(rootPath)) return null
        }
        cocosDownloadPath = rootPath + File.separator + "cocos" + File.separator
        return cocosDownloadPath
    }
    //endregion

    //region 版本更新路径
    fun apkDownloadPath(): String? {
        if (Files.createOrExistsDir(apkDownloadPath)) {
            return apkDownloadPath
        }
        //根路径处理
        if (!Files.isDir(rootPath)) {
            getRoot()
            if (!Files.isDir(rootPath)) return null
        }
        apkDownloadPath = rootPath + File.separator + "apk"
        return apkDownloadPath
    }
    //endregion


    //region 彩票玩法db路径
    private val LOT_DB_NAME = "bettypeview-v23.db"
    private var lotDbFilePath: String? = null//玩法数据库路径
    fun lotteryDbPath(): String? {
        if (Files.isFileExists(lotDbFilePath)) {
            return lotDbFilePath
        }
        val absolutePath = context.getDatabasePath(LOT_DB_NAME).absolutePath
        return if (Files.createOrExistsFile(absolutePath)) {
            if (Resources.copyFileFromAssets("database/${LOT_DB_NAME}", absolutePath)) {
                lotDbFilePath = absolutePath
                absolutePath
            } else null
        } else null
    }
    //endregion
}