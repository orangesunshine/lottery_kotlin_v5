package com.bdb.lottery.datasource.domain

import android.content.Context
import android.text.TextUtils
import androidx.core.net.toUri
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IDebugConfig
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.app.data.ConfigData
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.Configs
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class DomainRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val domainApi: DomainApi,
    private val domainLocalDs: DomainLocalDs,
    private val appApi: AppApi,
) {

    val domainError = {
        if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) context.toast("获取服务器域名失败")
    }

    //获取域名
    fun getDomain(domainSuccess: () -> Unit) {
        if (domainLocalDs.alreadySave.get()) {
            //已缓存域名
            domainSuccess()
        } else {
            //获取服务器域名
            val domainSave: (ConfigData?) -> Boolean = {
                val toUri = it?.WebMobileUrl?.toUri()
                val scheme = toUri?.scheme
                val host = toUri?.host
                val authority = toUri?.authority
                val port = toUri?.port
                val domain =
                    scheme + "://" + if (!host.isSpace()) host else authority + if (-1 != port) ":${port}" else ""
                domainLocalDs.saveDomain(domain)
            }

            val onlineDomainError = {
                //本地域名配置
                val localDomainStringId = context.resources.getIdentifier(
                    "local_http_url",
                    "string",
                    BuildConfig.APPLICATION_ID
                )

                if (localDomainStringId > 0) {
                    localDomain({
                        if (domainSave(it)) domainSuccess() else domainError()
                    }) {
                        domainError()
                    }
                }
            }

//            if (Configs.isDebug()) {
//                debugDomain({
//                    //线上域名获取成功
//                    if (domainSave(it)) domainSuccess() else onlineDomainError()
//                }, {
//                    onlineDomainError()
//                })
//            } else {
//                /**
//                 * 1.读取阿里云，七牛云域名配置文件，@拆分域名
//                 */
//            }
                onlineDomain({
                    //线上域名获取成功
                    if (domainSave(it)) domainSuccess() else onlineDomainError()
                }, {
                    onlineDomainError()
                })
        }
    }


    /**
     * 获取前端配置
     */
    fun debugDomain(
        success: ((ConfigData?) -> Any)? = null,
        error: ((String?) -> Any)? = null
    ) {
        observe(appApi.plateformParams(
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
    fun onlineDomain(
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
            observe(Observable.mergeArrayDelayError(*(onlineObservables.toTypedArray()))
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
            },
                { disposable = it },
                {
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
    fun localDomain(
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
                observe(
                    Observable.mergeArrayDelayError(*(localObservables.toTypedArray())),
                    {
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

                    },
                    { code, msg ->
                        //获取域名失败
                        if (!already.get()) {
                            Timber.d("local__onError：${msg}")
                        }
                    }, { disposable = it },
                    {
                        //数据解析问题
                        if (!already.get()) {
                            Timber.d("local__onComplete")
                            error?.let { it() }
                        }
                    })
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////

    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Unit)? = null,
        error: ((code: Int, msg: String?) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState>? = null
    ) {
        if (domainLocalDs.alreadySave.get() && domainLocalDs.getDomain().isDomainUrl()) {

        } else {

        }

        observable
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                onStart?.invoke(it)
                viewState?.setData(ViewState(true))
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccess()) {
                    success?.run { this(it.data) }
                } else {
                    BdbApp.context.toast(it.msg)
                    error?.run { this(it.code, it.msg) }
                }
            },
                {
                    Timber.d("observe__onError__throwable: ${it}")
                    error?.run { this(it.code, it.msg) }
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                },
                {
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                })
    }

    //错误返回有用数据
    fun <Data> observeErrorData(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Unit)? = null,
        error: ((BaseResponse<*>) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState>? = null
    ) {
        observable.subscribeOn(Schedulers.io())
            .doOnSubscribe {
                onStart?.invoke(it)
                viewState?.setData(ViewState(true))
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccess()) {
                    success?.run { this(it.data) }
                } else {
                    BdbApp.context.toast(it.msg)
                    error?.run { this(it.mappStringResponse()) }
                }
            },
                {
                    Timber.d("observe__onError__throwable: ${it}")
                    BdbApp.context.toast(it.msg)
                    if (it is ApiException) {
                        error?.run { this(it.response) }
                    } else {
                        error?.run { this(BaseResponse(it.code, it.msg, null)) }
                    }
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                },
                {
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                })
    }
}