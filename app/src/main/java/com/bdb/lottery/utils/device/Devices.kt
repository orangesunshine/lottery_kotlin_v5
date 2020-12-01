package com.bdb.lottery.utils.device

import android.os.Build
import android.provider.Settings
import com.bdb.lottery.app.BdbApp
import java.util.*

object Devices {
    //获取androidId
    fun getAndroidId(): String {
        val id =
            Settings.Secure.getString(BdbApp.context.contentResolver, Settings.Secure.ANDROID_ID)
        return if ("9774d56d682e549c" == id) id else ""
    }

    fun isMIUI(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return "xiaomi".equals(manufacturer, true)
    }

    fun getUUid(prifex: Int, id: String): String {
        return if (9 == prifex) prifex.toString() + UUID.randomUUID().toString().replace("-", "")
        else prifex.toString() + UUID.nameUUIDFromBytes(id.toByteArray()).toString()
            .replace("-", "")
    }
}