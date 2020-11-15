package com.bdb.lottery.utils

import android.text.TextUtils
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.const.IDebugConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 配置
 */
@Singleton
class TConfig @Inject constructor() {
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