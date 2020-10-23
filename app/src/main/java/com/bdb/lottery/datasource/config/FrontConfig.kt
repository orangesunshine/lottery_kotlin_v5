package com.bdb.lottery.datasource.config

import com.bdb.lottery.R
import com.bdb.lottery.app.LotteryApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isNetUrl
import com.tencent.mmkv.MMKV

object FrontConfig {
    private lateinit var mSelectDomain: String
    private val mmkv = MMKV.defaultMMKV()

    fun saveDomain(domain: String?) {
        if (domain.isNetUrl()) {
            mSelectDomain = domain!!
            mmkv.putString(ICache.CACHE_FRONT_CONFIG, domain)
        }
    }

    fun getDomain(): String {
        return if (mSelectDomain.isNetUrl()) mSelectDomain else mmkv.getString(
            ICache.CACHE_FRONT_CONFIG,
            LotteryApp.context.getString(R.string.local_http_url)
        )!!
    }
}