package com.bdb.lottery.datasource.domain

import android.text.TextUtils
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.const.DEBUGCONFIG
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.inject.TConfig
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class DomainRemoteDs @Inject constructor(
    private val tCache: TCache,
    private val tConfig: TConfig,
    private val domainApi: DomainApi,
    private val domainLocalDs: DomainLocalDs,
) {
    val cache = { platform: PlatformData? ->
        platform?.let {
            //保存配置
            tCache.cachePlatformParams(it)

            //保存rsa公钥
            it.rsaPublicKey.let {
                tCache.cacheRsaPublicKey(it)
            }
        }
    }

    //获取域名
    fun getDomain(domainSuccess: () -> Unit) {
        if (domainLocalDs.alreadySave.get() && domainLocalDs.getDomain().isDomainUrl()) {
            //已缓存域名
            Timber.d("getDomain->cache")
            domainSuccess()
        } else {
            Timber.d("getDomain->net")
            val domainError = {
                if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) BdbApp.context.toast("获取服务器域名失败")
            }

            //获取服务器域名
            val domainSave: (PlatformData?) -> Boolean = {
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
                val localDomainStringId = BdbApp.context.resources.getIdentifier(
                    "local_http_url",
                    "string",
                    BuildConfig.APPLICATION_ID
                )

                if (localDomainStringId > 0) {
                    localDomain(localDomainStringId, {
                        if (domainSave(it)) domainSuccess() else domainError()
                    }) {
                        domainError()
                    }
                }
            }


            if (tConfig.isDebug()) {
                debugDomain({
                    //线上域名获取成功
                    if (domainSave(it)) domainSuccess() else onlineDomainError()
                }, {
                    onlineDomainError()
                })
            } else {
                /**
                 * 1.读取阿里云，七牛云域名配置文件，@拆分域名
                 */
                onlineDomain({
                    //线上域名获取成功
                    if (domainSave(it)) domainSuccess() else onlineDomainError()
                }, {
                    onlineDomainError()
                })
            }
        }
    }


    /**
     * 获取前端配置
     */
    private fun debugDomain(
        success: ((PlatformData) -> Any)? = null,
        error: ((String?) -> Any)? = null,
    ) {
        observe(domainApi.urlPlatformParams(
            DEBUGCONFIG.URL_TEST_DOMAIN + URL.URL_PLATFORM_PARAMS
        ), {
            Timber.d("online__onNext__response: ${it}")
            //获取配置成功
            success?.run { this(it) }

            //缓存
            cache.invoke(it)
        }, { code, msg ->
            //获取域名失败
            Timber.d("online__onError__code：${code}, msg：${msg}")
            error?.run { this(msg) }
        })
    }

    /**
     * 线上服务器列表->域名配置->域名->前端配置
     * @param domains: 域名配置
     * @param success: 成功回调
     * @param error: 失败回调->对应平台配置local_http_url内置域名，调用getLocalDomain获取内置域名
     */
    private fun onlineDomain(
        success: ((PlatformData) -> Unit)? = null,
        error: ((String?) -> Unit)? = null,
    ) {
        Timber.d("getOnlineDomain")
        val configPath = BdbApp.context.getString(R.string.api_txt_path)
        val onlineObservables = mutableListOf<Observable<String>>()
        val hosts = URL.DOMAINS_API_TXT
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
                    if (!it.isSpace()) it.split("@") else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名配置列表：${it}")
                    if (!it.isNullOrEmpty()) Observable.fromIterable(it) else null
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("online域名：${it}")
                    if (it.isDomainUrl()) domainApi.urlPlatformParams(
                        it + URL.URL_PLATFORM_PARAMS
                    ) else null
                }, {
                //获取配置成功
                Timber.d("online__onNext__response: ${it}")
                if (already.compareAndSet(false, true)) {
                    //取消剩下网络请求
                    disposable?.let {
                        try {
                            if (!it.isDisposed) it.dispose()
                        } catch (e: Exception) {
                        }
                    }

                    //处理配置文件
                    success?.invoke(it)

                    //缓存
                    cache.invoke(it)
                }
            }, { _, msg ->
                //获取域名失败
                if (!already.get()) {
                    Timber.d("online__onError：${msg}")
                }
            },
                { disposable = it },
                {
                    //数据解析问题
                    if (!already.get()) {
                        Timber.d("online__onComplete")
                        error?.invoke("数据异常")
                    }
                })
        }
    }

    /**
     * 本地域名列表->域名->前端配置
     * @param success: 成功回调
     * @param error: 失败回调
     */
    private fun localDomain(
        @StringRes localDomainStringId: Int,
        success: ((PlatformData) -> Unit)? = null,
        error: (() -> Unit)? = null,
    ) {
        Timber.d("getLocalDomain")
        val localDomain = BdbApp.context.getString(localDomainStringId)
        if (!TextUtils.isEmpty(localDomain)) {
            val domains = localDomain.split("@")
            val localObservables = mutableListOf<Observable<BaseResponse<PlatformData>>>()
            if (!domains.isNullOrEmpty()) {
                for (domain in domains) {
                    localObservables.add(domainApi.urlPlatformParams(domain + URL.URL_PLATFORM_PARAMS))
                }
                var disposable: Disposable? = null
                val already = AtomicBoolean(false)
                observe(
                    Observable.mergeArrayDelayError(*(localObservables.toTypedArray())),
                    {
                        //获取配置成功
                        Timber.d("local__onNext__response: ${it}")
                        if (already.compareAndSet(false, true)) {
                            //取消剩下网络请求
                            disposable?.let {
                                try {
                                    if (!it.isDisposed) it.dispose()
                                } catch (e: Exception) {
                                }
                            }

                            //处理配置文件
                            success?.invoke(it)

                            //缓存
                            cache.invoke(it)
                        }

                    },
                    { _, msg ->
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
        success: ((Data) -> Unit)? = null,
        error: ((code: Int, msg: String?) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWrapper<ViewState>? = null,
    ) {
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

}