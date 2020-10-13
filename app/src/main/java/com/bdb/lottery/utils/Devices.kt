package com.bdb.lottery.utils

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.bdb.lottery.const.ICACHE
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

object Devices {
    lateinit var context: Context
    lateinit var mmkv: MMKV

    /**
     * 获取设备uuid
     */
    fun getDeviceUUid(): String {
        return (mmkv.getString(ICACHE.DEVICE_ID, null)) ?: let {
            val androidId = getAndroidId()
            saveUdid(if (TextUtils.isEmpty(androidId)) 9 else 2, androidId)
        }
    }

    fun getAndroidId(): String {
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return if ("9774d56d682e549c" == id) id else ""
    }

    fun saveUdid(prifex: Int, id: String): String {
        val udid = getUdid(prifex, id)
        mmkv.putString(ICACHE.DEVICE_ID, udid)
        return udid
    }

    fun getUdid(prifex: Int, id: String): String {
        return if (9 == prifex) prifex.toString() + UUID.randomUUID().toString().replace("-", "")
        else prifex.toString() + UUID.nameUUIDFromBytes(id.toByteArray()).toString()
            .replace("-", "")
    }
}