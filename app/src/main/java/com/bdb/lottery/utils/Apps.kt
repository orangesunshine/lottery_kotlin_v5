package com.bdb.lottery.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import android.text.TextUtils
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.extension.equalsNSpace
import timber.log.Timber
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileReader
import java.io.IOException
import java.security.Key
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


object Apps {

    /**
     * 判断是不是当前process
     */
    fun isMainProcess(): Boolean {
        // 获取当前包名
        val packageName = BdbApp.context.packageName
        // 获取当前进程名
        val processName = getProcessName(Process.myPid())
        Timber.d(
            "packageName: ${packageName}, processName: ${processName}, isMainProcess: ${
                processName.equalsNSpace(
                    packageName
                )
            }"
        )
        return processName.equalsNSpace(packageName)
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
            }
        }
        return null
    }

    //获取app版本name
    fun getAppVersionCode(context: Context?): Int {
        return context?.packageName?.let {
            context?.packageManager?.getPackageInfo(
                it,
                0
            )?.versionCode
        } ?: -1
    }

    //获取app版本name
    fun getAppVersionName(context: Context): String? {
        return context?.packageName?.let {
            context.packageManager?.getPackageInfo(
                it,
                0
            )?.versionName
        }
    }

    //杀死进程
    fun killApp() {
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    //获取application meta
    private fun getMetaDataFromApp(context: Context?, metadataNmae: String): String? {
        return context?.packageManager?.getApplicationInfo(
            context?.packageName,
            PackageManager.GET_META_DATA
        )?.metaData?.getString(metadataNmae) ?: null
    }

    //获取application "SCHEME" meta
    fun getScheme(context: Context?): String? {
        return getMetaDataFromApp(context, "SCHEME")
    }

    //判断平台
    fun isPlatform(context: Context?, scheme: String): Boolean {
        return scheme.equalsNSpace(getScheme(context))
    }

    //判断利博会
    fun isLBH(context: Context?): Boolean {
        return isPlatform(context, "libohui")
    }
}