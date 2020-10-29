package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.utils.Devices
import com.bdb.lottery.utils.cache.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeadersInterceptor @Inject constructor(val domainLocalDs: DomainLocalDs) :
    Interceptor {
    val deviceId = Devices.getDeviceUUid()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .header("C", "a")
                .header("D", deviceId)
                .apply {
                    Cache.getString(ICache.TOKEN_CACHE)
                        ?.let { if (!it.isBlank()) this.header("T", it) }
                }.build()
        )
    }

}