package com.bdb.lottery.datasource.appData

import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.utils.cache.Cache

//内存、缓存配置
object ConfigLocalDataSource {
    private var mSelectDomain: String? = null
    fun saveDomain(domain: String?) {
        if (domain.isDomainUrl()) {
            mSelectDomain = domain!!
            Cache.putString(ICache.CACHE_DOMAIN_URL, domain)
        }
    }

    fun getDomain(): String {
        if (!mSelectDomain.isDomainUrl()) {
            mSelectDomain = Cache.getString(
                ICache.CACHE_PLATEFORM,
                BdbApp.context.getString(R.string.local_http_url)
            )
        }
        return if (mSelectDomain.isDomainUrl()) mSelectDomain!! else BdbApp.context.getString(R.string.local_http_url)
    }
}