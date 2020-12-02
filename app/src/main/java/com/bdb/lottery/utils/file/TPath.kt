package com.bdb.lottery.utils.file

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.utils.sdcard.SDCards
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TPath @Inject constructor() {
    var rootPath: String? = null//下载根路径
    var cocosDownloadPath: String? = null//cocos下载路径
    var apkDownloadPath: String? = null//apk下载路径

    fun cacheRoot(): String? {
        val path = rootPath()
        if (Files.isDir(path)) rootPath = path
        return rootPath
    }

    fun rootPath(): String? {
        var path: String? = null
        if (!Files.isDir(rootPath)) {
            if (SDCards.isSDCardEnableByEnvironment()) path =
                BdbApp.sApp.getExternalFilesDir("app")?.path
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

    //cocos动画下载根路径
    fun cocosDownloadPath(): String? {
        if (Files.isDir(cocosDownloadPath)) {
            return cocosDownloadPath
        }
        if (!Files.isDir(rootPath)) {
            cacheRoot()
        }
        cocosDownloadPath = if (Files.isDir(rootPath)) {
            rootPath + File.separator + "cocos" + File.separator
        } else null
        return cocosDownloadPath
    }

    //版本更新路径
    fun apkDownloadPath(): String? {
        if (Files.isDir(apkDownloadPath)) {
            return apkDownloadPath
        }
        if (!Files.isDir(rootPath)) {
            cacheRoot()
        }
        apkDownloadPath = if (Files.isDir(rootPath)) {
            rootPath + File.separator + "cocos" + File.separator
        } else null
        return apkDownloadPath
    }
}