package com.bdb.lottery.datasource.domain

import android.content.Context
import android.text.TextUtils
import com.bdb.lottery.R
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IDebugConfig
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.app.data.ConfigData
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.RetrofitModule
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class DomainRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val domainApi: DomainApi,
    private val appApi: AppApi,
) {

    /**
     * 获取前端配置
     */
    fun getDebugFrontConfig(
        success: ((ConfigData?) -> Any)? = null,
        error: ((String?) -> Any)? = null
    ) {
        Retrofits.observe(appApi.plateformParams(
            IDebugConfig.URL_TEST_DOMAIN + HttpConstUrl.URL_CONFIG_FRONT
        ), {
            Timber.d("online__onNext__response: ${it}")
            //获取配置成功
            success?.run { this(it) }

            //保存rsa公钥
            it?.rsaPublicKey?.let {
                Cache.putString(ICache.PUBLIC_RSA_CACHE, it)
            }

            //保存plateform参数
            it?.platform?.let {
                Cache.putString(ICache.PLATEFORM_CACHE, it)
            }
        }, { code, msg ->
            //获取域名失败
            Timber.d("online__onError：${msg}")
            error?.run { this(msg) }
        })
    }

    /**
     * 线上服务器列表->域名配置->域名->前端配置
     * @param domains: 域名配置
     * @param success: 成功回调
     * @param error: 失败回调->对应平台配置local_http_url内置域名，调用getLocalDomain获取内置域名
     */
    fun getOnlineDomain(
        success: ((ConfigData?) -> Any)? = null,
        error: ((String?) -> Any)? = null
    ) {
        Timber.d("getOnlineDomain")
        val configPath = context.getString(R.string.api_txt_path)
        val onlineObservables = mutableListOf<Observable<String>>()
        val hosts = HttpConstUrl.DOMAINS_API_TXT
        if (!hosts.isNullOrEmpty()) {
            for (host in hosts) {
                if (host.isDomainUrl()) {
                    onlineObservables.add(domainApi.get(host + configPath))
                }
            }
        }

        if (!onlineObservables.isNullOrEmpty()) {
            var disposable: Disposable? = null
            val already = AtomicBoolean(false)
            Retrofits.observe(Observable.mergeArrayDelayError(*(onlineObservables.toTypedArray()))
                .doOnSubscribe { disposable = it }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    Timber.d("online域名配置：${it}")
                    if (it.isSpace()) it.split("@") else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名配置列表：${it}")
                    if (!it.isNullOrEmpty()) Observable.fromIterable(it) else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名：${it}")
                    if (it.isDomainUrl()) appApi.plateformParams(
                        it + HttpConstUrl.URL_CONFIG_FRONT
                    ) else null
                }, {
                //获取配置成功
                Timber.d("online__onNext__response: ${it}")
                it?.let {
                    if (already.compareAndSet(false, true)) {
                        //取消剩下网络请求
                        disposable?.let {
                            try {
                                if (!it.isDisposed) it.dispose()
                            } catch (e: Exception) {
                            }
                        }

                        //处理配置文件
                        success?.run { this(it) }

                        //保存rsa公钥
                        it?.rsaPublicKey?.let {
                            Cache.putString(ICache.PUBLIC_RSA_CACHE, it)
                        }

                        //保存plateform参数
                        it?.platform?.let {
                            Cache.putString(ICache.PLATEFORM_CACHE, it)
                        }
                    }
                }
            }, { code, msg ->
                //获取域名失败
                if (!already.get()) {
                    Timber.d("online__onError：${msg}")
                }
            }, {
                //数据解析问题
                if (!already.get()) {
                    Timber.d("local__onComplete")
                    error?.run { this("数据异常") }
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
        success: ((ConfigData?) -> Any)? = null,
        error: (() -> Any)? = null
    ) {
        Timber.d("getLocalDomain")
        val localDomain = context.getString(R.string.local_http_url)
        if (!TextUtils.isEmpty(localDomain)) {
            val domains = localDomain.split("@")
            val localObservables = mutableListOf<Observable<BaseResponse<ConfigData>>>()
            if (!domains.isNullOrEmpty()) {
                for (domain in domains) {
                    localObservables.add(appApi.plateformParams(domain + HttpConstUrl.URL_CONFIG_FRONT))
                }
                var disposable: Disposable? = null
                val already = AtomicBoolean(false)
                Retrofits.observe(Observable.mergeArrayDelayError(*(localObservables.toTypedArray()))
                    .doOnSubscribe { disposable = it }, {
                    //获取配置成功
                    Timber.d("local__onNext__response: ${it}")
                    it?.let {
                        if (already.compareAndSet(false, true)) {
                            //取消剩下网络请求
                            disposable?.let {
                                try {
                                    if (!it.isDisposed) it.dispose()
                                } catch (e: Exception) {
                                }
                            }

                            //处理配置文件
                            success?.run { this(it) }

                            //保存rsa公钥
                            it?.rsaPublicKey?.let {
                                Cache.putString(ICache.PUBLIC_RSA_CACHE, it)
                            }

                            //保存plateform参数
                            it?.platform?.let {
                                Cache.putString(ICache.PLATEFORM_CACHE, it)
                            }
                        }
                    }

                }, { code, msg ->
                    //获取域名失败
                    if (!already.get()) {
                        Timber.d("local__onError：${msg}")
                    }
                }, complete = {
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