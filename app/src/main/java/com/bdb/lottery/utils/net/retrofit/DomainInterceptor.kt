package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isDomainUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DomainInterceptor @Inject constructor(val domainLocalDs: DomainLocalDs) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val interceptorDomain = request.headers.get("domainIntercept")?.toBoolean() ?: true
        Timber.d("interceptorDomain: ${interceptorDomain}")
        if (interceptorDomain) {
            val domain = domainLocalDs.getDomain()
            Timber.d("domain: ${domain}")
            if (domain.isDomainUrl() && domainLocalDs.alreadySave.get()) {
                val domainUrl = domain!!.toHttpUrl()
                request = chain.request().newBuilder().url(
                    request.url.newBuilder()
                        .scheme(domainUrl.scheme)
                        .host(domainUrl.host)
                        .build()
                ).build()
            }
        }
        return chain.proceed(request)
    }

}