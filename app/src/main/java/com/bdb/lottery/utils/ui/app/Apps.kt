package com.bdb.lottery.utils.ui.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import android.text.TextUtils
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isSpace
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object Apps {
    private fun getMetaDataFromApp(metadataName: String): String? {
        return getMetaDataFromApp(BdbApp.context, metadataName)
    }

    /**
     * application meta
     */
    private fun getMetaDataFromApp(context: Context, metadataName: String): String? {
        return context.packageManager?.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )?.metaData?.getString(metadataName)
    }

    /**
     * 获取application "SCHEME" meta
     */
    private fun getScheme(): String? {
        return getMetaDataFromApp("SCHEME")
    }

    /**
     * 根据application "SCHEME" meta 判断平台
     */
    fun isPlatform(scheme: String): Boolean {
        return scheme.equalsNSpace(getScheme())
    }

    //杀死进程
    fun killApp() {
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    //获取AppVersionName
    fun getAppVersionName(): String? {
        return getAppVersionName(BdbApp.context)
    }

    private fun getAppVersionName(context: Context): String? {
        return context.packageName?.let {
            context.packageManager?.getPackageInfo(
                it,
                0
            )?.versionName
        }
    }

    //获取AppVersionCode
    fun getAppVersionCode(): Int {
        return getAppVersionCode(BdbApp.context)
    }

    fun getAppVersionCode(context: Context): Int {
        val packageName = context.packageName
        if (packageName.isSpace()) return -1
        return packageName.let {
            context.packageManager.getPackageInfo(
                it,
                0
            )?.versionCode ?: -1
        }
    }

    /**
     * Return the name of current process.
     */
    fun getCurrentProcessName(): String? {
        var name: String? = getCurrentProcessNameByFile()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByAms()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByReflect()
        return name
    }

    private fun getCurrentProcessNameByFile(): String? {
        return try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getCurrentProcessNameByAms(): String? {
        return getCurrentProcessNameByAms(BdbApp.context)
    }

    private fun getCurrentProcessNameByAms(context: Context): String? {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) return ""
            val pid = Process.myPid()
            for (aInfo in info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            return ""
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String? {
        var processName = ""
        try {
            val app: Application = BdbApp.sApp
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return processName
    }
}