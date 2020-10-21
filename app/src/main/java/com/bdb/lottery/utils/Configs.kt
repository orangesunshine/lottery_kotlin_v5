package com.bdb.lottery.utils

import android.text.TextUtils
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.const.IDebugConfig

/**
 * 配置
 */
object Configs {
    /**
     * 判断是不是debug
     */
    fun isDebug(): Boolean {
        return TextUtils.equals("debug", BuildConfig.BUILD_TYPE)
    }

    /**
     * bugly平台id
     */
    fun buglyAppId(): String {
        return if (isDebug()) IDebugConfig.BUGLY_APPID else BuildConfig.BUGLY_APPID
    }
}