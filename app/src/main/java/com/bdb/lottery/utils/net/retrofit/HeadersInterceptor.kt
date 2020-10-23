package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.utils.Devices
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeadersInterceptor : Interceptor {
    val deviceId = Devices.getDeviceUUid()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .header("C", "a")
                .header("D", deviceId)
                .build()
        )
    }

}