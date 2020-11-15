package com.bdb.lottery.datasource.domain

import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.utils.cache.TCache
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

//内存、缓存配置
@Singleton
class DomainLocalDs @Inject constructor(val tCache: TCache) {
    private var mSelectDomain: String? = null
    var alreadySave: AtomicBoolean = AtomicBoolean(false)

    fun saveDomain(domain: String): Boolean {
        return domain.isDomainUrl().apply {
            alreadySave.set(this)
            if (this) {
                mSelectDomain = domain
                tCache.putString(ICache.DOMAIN_URL_CACHE, domain)
            }
        }
    }

    fun getDomain(): String? {
        if (!mSelectDomain.isDomainUrl() && alreadySave.get()) {
            mSelectDomain = tCache.getString(ICache.DOMAIN_URL_CACHE)
        }
        return mSelectDomain
    }

    fun clearDomain() {
        alreadySave.set(false)
        mSelectDomain = null
        tCache.putString(ICache.DOMAIN_URL_CACHE)
    }
}