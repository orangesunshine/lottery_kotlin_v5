package com.bdb.lottery.utils

import android.provider.Settings
import android.text.TextUtils
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.utils.cache.Cache
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Devices @Inject constructor() {
    /**
     * 获取设备uuid
     */
    fun getDeviceUUid(): String {
        return (Cache.getString(ICache.DEVICE_ID_CACHE, null)) ?: let {
            val androidId = getAndroidId()
            saveUdid(if (TextUtils.isEmpty(androidId)) 9 else 2, androidId)
        }
    }

    fun getAndroidId(): String {
        val id =
            Settings.Secure.getString(BdbApp.context.contentResolver, Settings.Secure.ANDROID_ID)
        return if ("9774d56d682e549c" == id) id else ""
    }

    fun saveUdid(prifex: Int, id: String): String {
        val udid = getUdid(prifex, id)
        Cache.putString(ICache.DEVICE_ID_CACHE, udid)
        return udid
    }

    fun getUdid(prifex: Int, id: String): String {
        return if (9 == prifex) prifex.toString() + UUID.randomUUID().toString().replace("-", "")
        else prifex.toString() + UUID.nameUUIDFromBytes(id.toByteArray()).toString()
            .replace("-", "")
    }
}