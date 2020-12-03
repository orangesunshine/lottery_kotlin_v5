package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.device.TDevice
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
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
        var request = chain.request()
        val interceptorHeader = request.headers.get("headerIntercept")?.toBoolean() ?: true
        Timber.d("interceptorHeader: ${interceptorHeader}")
        if (interceptorHeader) {
            val token = tCache.tokenCache()
            request = chain.request().newBuilder()
                .header("C", "a")
                .header("D", deviceId)
                .apply { if (!token.isSpace()) header("T", token!!) }
                .build()
        }

        val cocos: String? = request.headers.get("cocos")
        return if (cocos.isSpace()) {
            chain.proceed(request)
        } else {
            val proceed = chain.proceed(request)
            proceed.newBuilder().addHeader("cocos", cocos!!).build()
        }
    }

}
