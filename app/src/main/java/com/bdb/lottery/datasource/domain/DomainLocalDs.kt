package com.bdb.lottery.datasource.domain

import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.utils.cache.Cache
import javax.inject.Inject
import javax.inject.Singleton

//内存、缓存配置
@Singleton
class DomainLocalDs @Inject constructor() {
    private var mSelectDomain: String? = null
    fun saveDomain(domain: String?) {
        if (domain.isDomainUrl()) {
            mSelectDomain = domain!!
            Cache.putString(ICache.DOMAIN_URL_CACHE, domain)
        }
    }

    fun getDomain(): String {
        if (!mSelectDomain.isDomainUrl()) {
            mSelectDomain = Cache.getString(
                ICache.CUSTOM_SERVICE_URL_CACHE,
                getFirstLocalDomain()
            )
        }
        return if (mSelectDomain.isDomainUrl()) mSelectDomain!! else getFirstLocalDomain()
    }

    fun getFirstLocalDomain(): String {
        val local = BdbApp.context.getString(R.string.local_http_url)
        return if (local.contains("@")) local.split("@")[0] else local
    }
}