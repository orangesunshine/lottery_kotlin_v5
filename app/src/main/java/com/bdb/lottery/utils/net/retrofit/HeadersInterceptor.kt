package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.utils.Devices
import com.bdb.lottery.utils.cache.Cache
import okhttp3.Interceptor
import okhttp3.Response
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
                .header(
                    "T", Cache.getString(ICache.TOKEN_CACHE)
                ).build()
        )
    }

}