package com.bdb.lottery.utils.retrofit

import com.bdb.lottery.const.IConst
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object Retrofits {
    val logInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }
    val okClient = OkHttpClient.Builder().addInterceptor(logInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)  //设置超时时间 15s
        .readTimeout(5, TimeUnit.SECONDS)     //设置读取超时时间
        .writeTimeout(5, TimeUnit.SECONDS).build()   //设置写入超时时间


    fun create(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 使用RxJava作为回调适配器
            .client(okClient)
            .baseUrl(IConst.BASE_URL) //获取 最终的base url
            /*.baseUrl(App.getInstance().getRealUrl())//此处选择 api 的真实网址*/
            .build()
    }
}