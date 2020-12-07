package com.bdb.lottery.utils.cache

import com.tencent.mmkv.MMKV

object Caches {
    private val mmkv = MMKV.defaultMMKV()

    fun putString(cacheKey: String, cacheValue: String? = null) {
        mmkv.putString(cacheKey, cacheValue)
    }

    fun getString(cacheKey: String, default: String? = null): String? {
        return mmkv.getString(cacheKey, default)
    }

    fun putBoolean(cacheKey: String, cacheValue: Boolean = false) {
        mmkv.putBoolean(cacheKey, cacheValue)
    }

    fun getBoolean(cacheKey: String, default: Boolean = false): Boolean {
        return mmkv.getBoolean(cacheKey, default)
    }


    fun putLong(cacheKey: String, cacheValue: Long = -1) {
        mmkv.putLong(cacheKey, cacheValue)
    }

    fun getLong(cacheKey: String, default: Long = -1): Long {
        return mmkv.getLong(cacheKey, default)
    }

    fun putInt(cacheKey: String, cacheValue: Int = -1) {
        mmkv.putInt(cacheKey, cacheValue)
    }

    fun getInt(cacheKey: String, default: Int = -1): Int {
        return mmkv.getInt(cacheKey, default)
    }
}