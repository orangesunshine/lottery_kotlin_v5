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
        val request = chain.request()
        val domain = domainLocalDs.getDomain()
        Timber.d("domain: ${domain}")
        return chain.proceed(
            if (domain.isDomainUrl() && domainLocalDs.alreadySave.get()) {
                val domainUrl = domain!!.toHttpUrl()
                Timber.d("requestUrl: ${request.url}, domainUrl: ${domainUrl}")
                chain.request().newBuilder().url(
                    request.url.newBuilder()
                        .scheme(domainUrl.scheme)
                        .host(domainUrl.host)
                        .build()
                ).build()
            } else {
                request
            }
        )
    }

}