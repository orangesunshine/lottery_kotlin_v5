package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.IConst
import com.bdb.lottery.extension.isSpace
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory


object Retrofits {
    val logInterceptor = HttpLoggingInterceptor({
        Timber.d(it)
    }).also {
        it.level = HttpLoggingInterceptor.Level.BASIC
    }

    fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(SsX509TrustManager), SecureRandom())
        return sslContext.getSocketFactory()
    }

    val okClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .addInterceptor(HeadersInterceptor())
        .sslSocketFactory(getSSLSocketFactory(), SsX509TrustManager)
        .connectTimeout(15, TimeUnit.SECONDS)  //设置超时时间 15s
        .readTimeout(5, TimeUnit.SECONDS)     //设置读取超时时间
        .writeTimeout(5, TimeUnit.SECONDS).build()   //设置写入超时时间


    /**
     * 域名专用，超时6秒
     */
    fun create(): Retrofit {
        val okClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(HeadersInterceptor())
            .sslSocketFactory(getSSLSocketFactory(), SsX509TrustManager)
            .connectTimeout(6, TimeUnit.SECONDS)  //设置超时时间 6s
            .readTimeout(5, TimeUnit.SECONDS)     //设置读取超时时间
            .writeTimeout(5, TimeUnit.SECONDS).build()   //设置写入超时时间
        return create(IConst.BASE_URL, okClient)
    }

    /**
     * 公用
     */
    fun create(url: String, okHttpClient: OkHttpClient = okClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create()) // 使用Gson作为数据转换器
            .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 使用RxJava作为回调适配器
            .client(okClient)
            .baseUrl(url) //获取 最终的base url
            /*.baseUrl(App.getInstance().getRealUrl())//此处选择 api 的真实网址*/
            .build()
    }

    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Any?)? = null,
        error: ((code: Int, msg: String?) -> Any)? = null,
        complete: (() -> Any?)? = null,
    ) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    if (!it.successData()) {
                        if (error != null) {
                            error(code, msg)
                        }
                        return@subscribe
                    }
                }
                success?.run { this(it.data) }
            },
                {
                    error?.run { this(code(it), msg(it)) }
                    complete?.run { this() }
                },
                {
                    complete?.run { this() }
                })
    }

    //返回code
    fun code(throwable: Throwable?): Int {
        throwable?.let {
            if (it is HttpException) {
                return it.code()
            }
        }
        return IConst.DEFAULT_ERROR_CODE
    }

    //错误信息
    fun msg(throwable: Throwable?): String? {
        return throwable?.let {
            val message = it.message
            return if (message.isSpace()) message else it.cause?.message
        }
    }
}