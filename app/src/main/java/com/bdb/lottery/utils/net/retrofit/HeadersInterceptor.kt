package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.TDevice
import com.bdb.lottery.utils.cache.TCache
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeadersInterceptor @Inject constructor(
    val domainLocalDs: DomainLocalDs,
    tDevice: TDevice,
    private val tCache: TCache,
) :
    Interceptor {
    private val deviceId = tDevice.getDeviceUUid()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tCache.getString(ICache.TOKEN_CACHE)
        return chain.proceed(
            chain.request().newBuilder()
                .header("C", "a")
                .header("D", deviceId)
                .apply { if (!token.isSpace()) header("T", token!!) }
                .build()
        )
    }

}