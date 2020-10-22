package com.bdb.lottery.utils.net.retrofit

import android.text.TextUtils
import com.bdb.lottery.const.IConst
import com.bdb.lottery.extension.notEmpty
import com.bdb.lottery.utils.net.NetCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


object Retrofits {
    val logInterceptor = HttpLoggingInterceptor({
        Timber.d(it)
    }).also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }
    val okClient = OkHttpClient.Builder().addInterceptor(logInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)  //设置超时时间 15s
        .readTimeout(5, TimeUnit.SECONDS)     //设置读取超时时间
        .writeTimeout(5, TimeUnit.SECONDS).build()   //设置写入超时时间


    fun create(): Retrofit {
        return create(IConst.BASE_URL)
    }

    fun create(url: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 使用RxJava作为回调适配器
            .client(okClient)
            .baseUrl(url) //获取 最终的base url
            /*.baseUrl(App.getInstance().getRealUrl())//此处选择 api 的真实网址*/
            .build()
    }

    fun <Data> observe(observable: Observable<Data>, callback: NetCallback<Data>) {
        observable?.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ callback?.onSuccess(it) },
                {
                    callback?.onError(code(it), msg(it))
                    callback?.onComplete()
                },
                { callback?.onComplete() })
    }

    fun code(throwable: Throwable): Int {
        throwable?.let {
            if (it is HttpException) {
                return it.code()
            }
        }
        return IConst.DEFAULT_ERROR_CODE
    }

    fun msg(throwable: Throwable): String? {
        return throwable?.let {
            val message = it.message
            return if (message.notEmpty()) message else it.cause?.message
        }
    }
}