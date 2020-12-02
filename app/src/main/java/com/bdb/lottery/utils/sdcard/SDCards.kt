package com.bdb.lottery.utils.sdcard

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.format.Formatter
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.utils.file.Files
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.util.*

object SDCards {
    //region 是否挂载
    fun isSDCardEnableByEnvironment(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
    //endregion

    //region 获取外置sd卡路径
    fun getSDCardPathByEnvironment(): String? {
        return if (isSDCardEnableByEnvironment()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }
    //endregion

    //region sd卡列表
    fun getSDCardInfo(): List<SDCardInfo>? {
        val paths: MutableList<SDCardInfo> = ArrayList()
        val sm = BdbApp.sApp.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            ?: return paths
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageVolumes = sm.storageVolumes
            try {
                val getPathMethod = StorageVolume::class.java.getMethod("getPath")
                for (storageVolume in storageVolumes) {
                    val isRemovable = storageVolume.isRemovable
                    val state = storageVolume.state
                    val path = getPathMethod.invoke(storageVolume) as String
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            try {
                val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                val getPathMethod = storageVolumeClazz.getMethod("getPath")
                val isRemovableMethod = storageVolumeClazz.getMethod("isRemovable")
                val getVolumeStateMethod =
                    StorageManager::class.java.getMethod("getVolumeState", String::class.java)
                val getVolumeListMethod = StorageManager::class.java.getMethod("getVolumeList")
                val result = getVolumeListMethod.invoke(sm)
                val length = Array.getLength(result)
                for (i in 0 until length) {
                    val storageVolumeElement = Array.get(result, i)
                    val path = getPathMethod.invoke(storageVolumeElement) as String
                    val isRemovable = isRemovableMethod.invoke(storageVolumeElement) as Boolean
                    val state = getVolumeStateMethod.invoke(sm, path) as String
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return paths
    }
    //endregion

    //region 获取挂载sd卡路径列表
    fun getMountedSDCardPath(): List<String>? {
        val path: MutableList<String> = ArrayList()
        val sdCardInfo = getSDCardInfo()
        if (sdCardInfo == null || sdCardInfo.isEmpty()) return path
        for (cardInfo in sdCardInfo) {
            val state = cardInfo.state ?: continue
            if ("mounted" == state.toLowerCase()) {
                path.add(cardInfo.path)
            }
        }
        return path
    }
    //endregion

    //region 外置sd卡大小
    fun getExternalTotalSize(): Long {
        return Files.getFsTotalSize(getSDCardPathByEnvironment())
    }

    fun getExternalAvailableSize(): Long {
        return Files.getFsAvailableSize(getSDCardPathByEnvironment())
    }
    //endregion

    //region 内置sd卡大小
    fun getInternalTotalSize(): Long {
        return Files.getFsTotalSize(Environment.getDataDirectory().absolutePath)
    }

    fun getInternalAvailableSize(): Long {
        return Files.getFsAvailableSize(Environment.getDataDirectory().absolutePath)
    }
    //endregion

    class SDCardInfo internal constructor(
        val path: String,
        val state: String,
        val isRemovable: Boolean
    ) {
        val totalSize: Long
        val availableSize: Long

        override fun toString(): String {
            return "SDCardInfo {" +
                    "path = " + path +
                    ", state = " + state +
                    ", isRemovable = " + isRemovable +
                    ", totalSize = " + Formatter.formatFileSize(BdbApp.sApp, totalSize) +
                    ", availableSize = " + Formatter.formatFileSize(BdbApp.sApp, availableSize) +
                    '}'
        }

        init {
            totalSize = Files.getFsTotalSize(path)
            availableSize = Files.getFsAvailableSize(path)
        }
    }
}