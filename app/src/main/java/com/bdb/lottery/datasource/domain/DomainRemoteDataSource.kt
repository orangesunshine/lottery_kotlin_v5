package com.bdb.lottery.datasource.domain

import android.content.Context
import android.text.TextUtils
import com.bdb.lottery.R
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.app.data.DataConfig
import com.bdb.lottery.datasource.common.string.CommonStringApi
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.nNullEmpty
import com.bdb.lottery.utils.cache.Cache
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named


class DomainRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("Url") private var retrofit: Retrofit
) {
    val stringApi = retrofit.create(CommonStringApi::class.java)
    val configApi = retrofit.create(AppApi::class.java)

    /**
     * 线上服务器列表->域名配置->域名->前端配置
     * @param domains: 域名配置
     * @param success: 成功回调
     * @param error: 失败回调->对应平台配置local_http_url内置域名，调用getLocalDomain获取内置域名
     */
    fun getOnlineDomain(
        success: ((DataConfig?) -> Any)? = null,
        error: (() -> Any)? = null
    ) {
        Timber.d("getOnlineDomain")
        val configPath = context.getString(R.string.api_txt_path)
        val onlineObservables = mutableListOf<Observable<String>>()
        val hosts = HttpConstUrl.DOMAINS_API_TXT
        if (!hosts.isNullOrEmpty()) {
            for (host in hosts) {
                if (host.isDomainUrl()) {
                    onlineObservables.add(stringApi.get(host + configPath))
                }
            }
        }

        if (!onlineObservables.isNullOrEmpty()) {
            var disposable: Disposable? = null
            val already = AtomicBoolean(false)
            Observable.mergeArrayDelayError(*(onlineObservables.toTypedArray()))
                .doOnSubscribe { disposable = it }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    Timber.d("online域名配置：${it}")
                    if (it.nNullEmpty()) it.split("@") else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名配置列表：${it}")
                    if (!it.isNullOrEmpty()) Observable.fromIterable(it) else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名：${it}")
                    if (it.isDomainUrl()) configApi.config(
                        it + HttpConstUrl.URL_CONFIG_FRONT
                    ) else null
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("online__onNext__response: ${it}")
                    //获取配置成功
                    if (it.isSuccess() && null != it && null != it.data) {
                        if (already.compareAndSet(false, true)) {
                            //取消剩下网络请求
                            disposable?.let {
                                try {
                                    if (!it.isDisposed) it.dispose()
                                } catch (e: Exception) {
                                }
                            }

                            //处理配置文件
                            success?.run { this(it.data) }

                            //保存rsa公钥
                            it.data?.rsaPublicKey?.let {
                                Cache.putString(ICache.CACHE_PUBLIC_RSA, it)
                            }

                            //保存plateform参数
                            it.data?.platform?.let {
                                Cache.putString(ICache.CACHE_PLATEFORM, it)
                            }
                        }
                    }
                }, {
                    //获取域名失败
                    if (!already.get()) {
                        Timber.d("online__onError：${it.msg}")
                        error?.let { it() }
                    }
                }, {
                    //数据解析问题
                    if (!already.get()) {
                        Timber.d("local__onComplete")
                        error?.let { it() }
                    }
                })
        }
    }

    /**
     * 本地域名列表->域名->前端配置
     * @param success: 成功回调
     * @param error: 失败回调
     */
    fun getLocalDomain(
        success: ((DataConfig?) -> Any)? = null,
        error: (() -> Any)? = null
    ) {
        Timber.d("getLocalDomain")
        val localDomain = context.getString(R.string.local_http_url)
        if (!TextUtils.isEmpty(localDomain)) {
            val domains = localDomain.split("@")
            val localObservables = mutableListOf<Observable<BaseResponse<DataConfig>>>()
            if (!domains.isNullOrEmpty()) {
                for (domain in domains) {
                    localObservables.add(configApi.config(domain + HttpConstUrl.URL_CONFIG_FRONT))
                }
                var disposable: Disposable? = null
                val already = AtomicBoolean(false)
                Observable.mergeArrayDelayError(*(localObservables.toTypedArray()))
                    .doOnSubscribe { disposable = it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //获取配置成功
                        Timber.d("local__onNext__response: ${it}")
                        if (it.isSuccess() && null != it && null != it.data) {
                            if (already.compareAndSet(false, true)) {
                                //取消剩下网络请求
                                disposable?.let {
                                    try {
                                        if (!it.isDisposed) it.dispose()
                                    } catch (e: Exception) {
                                    }
                                }

                                //处理配置文件
                                success?.run { this(it.data) }

                                //保存rsa公钥
                                it.data?.rsaPublicKey?.let {
                                    Cache.putString(ICache.CACHE_PUBLIC_RSA, it)
                                }

                                //保存plateform参数
                                it.data?.platform?.let {
                                    Cache.putString(ICache.CACHE_PLATEFORM, it)
                                }
                            }
                        }
                    }, {
                        //获取域名失败
                        if (!already.get()) {
                            Timber.d("local__onError：${it.msg}")
                            error?.let { it() }
                        }
                    }, {
                        //数据解析问题
                        if (!already.get()) {
                            Timber.d("local__onComplete")
                            error?.let { it() }
                        }
                    })
            }
        }
    }
}