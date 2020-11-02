package com.bdb.lottery.datasource.domain

import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.utils.cache.Cache
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

//内存、缓存配置
@Singleton
class DomainLocalDs @Inject constructor() {
    private var mSelectDomain: String? = null
    var alreadySave: AtomicBoolean = AtomicBoolean(false)


    fun saveDomain(domain: String): Boolean {
        return domain.isDomainUrl().apply {
            alreadySave.set(this)
            if (this) {
                mSelectDomain = domain
                Cache.putString(ICache.DOMAIN_URL_CACHE, domain)
            }
        }
    }

    fun getDomain(): String? {
        if (!mSelectDomain.isDomainUrl() && alreadySave.get()) {
            mSelectDomain = Cache.getString(
                ICache.DOMAIN_URL_CACHE, null
            )
        }
        return mSelectDomain
    }
}