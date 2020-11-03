package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.const.IConst
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.utils.Devices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

@Module
@InstallIn(ApplicationComponent::class)
class RetrofitModule {
    val DEFAULT_TIMEOUT: Long = 15//默认网络超时

    /**
     * 公用
     */
    @Provides
    fun create(
        logInterceptor: HttpLoggingInterceptor,
        headersInterceptor: HeadersInterceptor,
        domainInterceptor: DomainInterceptor,
    ): Retrofit {
        val okClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(headersInterceptor)
            .addInterceptor(domainInterceptor)
            .sslSocketFactory(getSSLSocketFactory(), SsX509TrustManager)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)  //设置超时时间 15s
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)     //设置读取超时时间
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build()   //设置写入超时时间
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create()) // 使用plaintext作为数据转换器
            .addConverterFactory(BdbGsonConverterFactory.create()) // 使用Gson作为数据转换器
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 使用RxJava作为回调适配器
            .client(okClient)
            .baseUrl(IConst.BASE_URL) //获取 最终的base url
            .build()
    }

    /**
     * 日志拦截器
     */
    @Provides
    @Singleton
    fun provideHttpLogIntercept(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor {
            Timber.d(it)
        }.also {
            it.level = HttpLoggingInterceptor.Level.BASIC
        }
    }


    /**
     * ssl tls
     */
    @Provides
    @Singleton
    fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(SsX509TrustManager), SecureRandom())
        return sslContext.getSocketFactory()
    }

    /**
     * 头部header修改
     */
    @Provides
    @Singleton
    fun provideHeadersInterceptor(
        domainLocalDs: DomainLocalDs,
        devices: Devices
    ): HeadersInterceptor {
        return HeadersInterceptor(domainLocalDs, devices)
    }

    /**
     * 域名修改
     */
    @Provides
    @Singleton
    fun provideDomainInterceptor(
        domainLocalDs: DomainLocalDs
    ): DomainInterceptor {
        return DomainInterceptor(domainLocalDs)
    }
}