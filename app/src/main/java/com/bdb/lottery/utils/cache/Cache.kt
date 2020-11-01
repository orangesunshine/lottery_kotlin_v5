package com.bdb.lottery.utils.cache

import com.tencent.mmkv.MMKV

/**
 * 统一缓存方法，mmkv可以替换
 */
object Cache {
    val mmkv = MMKV.defaultMMKV()

    fun putString(cacheKey: String, cacheValue: String?) {
        mmkv.putString(cacheKey, cacheValue)
    }

    fun getString(cacheKey: String, default: String? = ""): String? {
        return mmkv.getString(cacheKey, default)
    }

    fun putBoolean(cacheKey: String, cacheValue: Boolean) {
        mmkv.putBoolean(cacheKey, cacheValue)
    }

    fun getBoolean(cacheKey: String, default: Boolean = false): Boolean {
        return mmkv.getBoolean(cacheKey, default)
    }


    fun putLong(cacheKey: String, cacheValue: Long) {
        mmkv.putLong(cacheKey, cacheValue)
    }

    fun getLong(cacheKey: String, default: Long = -1): Long {
        return mmkv.getLong(cacheKey, default)
    }
}