package com.bdb.lottery.utils.device

import com.bdb.lottery.utils.cache.TCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TDevice @Inject constructor(val tCache: TCache) {
    /**
     * 获取设备uuid
     */
    fun getDeviceUUid(): String {
        return tCache.cachePriUUid()
    }
}