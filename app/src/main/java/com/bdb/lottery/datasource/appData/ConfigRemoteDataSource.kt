package com.bdb.lottery.datasource.appData

import android.content.Context
import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IConst
import com.bdb.lottery.datasource.appData.data.ApkVersion
import com.bdb.lottery.datasource.appData.data.ConfigData
import com.bdb.lottery.datasource.appData.data.CustomServiceData
import com.bdb.lottery.datasource.string.StringApi
import com.bdb.lottery.extension.isDomainUrl
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.nNullEmpty
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.NetCallback
import com.bdb.lottery.utils.net.retrofit.Retrofits
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named


class ConfigRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("Url") private var retrofit: Retrofit
) {
    val stringApi = retrofit.create(StringApi::class.java)
    val configApi = retrofit.create(ConfigApi::class.java)

    /**
     * 获取域名并回调
     */
    fun getDomainNdCallback(block: (ConfigData?) -> Any) {
        val domainPath = context.getString(R.string.api_txt_path)

        if (domainPath.nNullEmpty()) {
            val domainObservables = domainObservables(HttpConstUrl.DOMAINS_API_TXT)
            getDomainNdCallbackReal(domainObservables, block, local = false)
        }
    }

    /**
     * @param domainObservables: 域名配置请求
     * @param block: 回调
     * @param local: 是否本地域名
     */
    fun getDomainNdCallbackReal(
        domainObservables: Array<Observable<String>>,
        success: ((ConfigData?) -> Any)? = null,
        error: (() -> Any)? = null,
        local: Boolean = false
    ) {
        val already = AtomicBoolean(false)
        var disposable: Disposable? = null
        if (!domainObservables.isNullOrEmpty()) {
            Observable.mergeArrayDelayError(*domainObservables)
                .doOnSubscribe { disposable = it }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    Timber.d("域名配置：${it}")
                    it.split("@")
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("域名配置列表：${it}")
                    Observable.fromIterable(it)
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Timber.d("域名：${it}")
                    if (disposable?.isDisposed()
                            ?: true
                    ) null else configApi.config(it + HttpConstUrl.URL_CONFIG_FRONT)
                }
                .observeOn(Schedulers.io())
                .onErrorReturn {
                    Timber.d("域名错误：${it}")
                    val response = BaseResponse<ConfigData>()
                    response.code =
                        if (null != it && it is HttpException) it.code() else IConst.DOAMIN_ERROR_CODE
                    response.msg = it.msg
                    response
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //获取配置成功
                    if (it.isSuccess() && null != it && null != it.data) {
                        if (already.compareAndSet(false, true)) {
                            Timber.d("onNext__response: ${it}")
                            //取消剩下网络请求
                            disposable?.let {
                                try {
                                    if (!it.isDisposed) it.dispose()
                                } catch (e: Exception) {
                                }
                            }

                            //处理配置文件
                            success?.run { this(it?.data) }

                            //保存rsa公钥
                            it?.data?.rsaPublicKey?.let {
                                Cache.putString(ICache.CACHE_PUBLIC_RSA, it)
                            }

                            //保存plateform参数
                            it?.data?.platform?.let {
                                Cache.putString(ICache.CACHE_PLATEFORM, it)
                            }
                        }
                    }
                }, {
                    //获取域名失败
                    Timber.d("onError：${it.msg}, local: ${local}")
                    error?.let { it() }
                })
        } else {
            //获取域名失败
            Timber.d("onError__local: ${local}")
            error?.let { it() }
        }
    }

    fun onError(block: (ConfigData?) -> Any, local: Boolean) {
        if (local) {
            //本地配置域名失败，回调
            block(null)
        } else {
            //线上域名获取失败，读取本地域名配置
            val domains = BdbApp.context.getString(R.string.api_txt_path)
            if (domains.nNullEmpty()) {
                getDomainNdCallbackReal(
                    domainObservables(
                        domains.split("@").toTypedArray()
                    ), block, local = true
                )
            }
        }
    }

    /**
     * 根据域名配置列表，生成域名请求observable
     */
    fun domainObservables(domains: Array<String>): Array<Observable<String>> {
        val domainObservables = mutableListOf<Observable<String>>()
        val domainPath = context.getString(R.string.api_txt_path)
        if (domainPath.nNullEmpty()) {
            if (!domains.isNullOrEmpty()) {
                for (domain in domains) {
                    if (domain.isDomainUrl()) {
                        domainObservables.add(stringApi.get(domain + domainPath))
                    }
                }
            }
        }
        return domainObservables?.toTypedArray()
    }

    //客服
    fun getCustomServiceUrl(callback: NetCallback<CustomServiceData>) {
        Retrofits.observe(configApi.customService(), success = {
            it?.let {
                if (it.kefuxian.nNullEmpty())
                    Cache.putString(ICache.CACHE_CUSTOM_SERVICE_URL, it.kefuxian)
            }
        })
    }

    //获取apk版本信息
    fun getAPkVeresion(callback: NetCallback<ApkVersion>) {
        Retrofits.observe(configApi.apkversion(), success = {
            it?.let {
                Cache.putString(ICache.CACHE_APK_VERSION, Gson().toJson(it))
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        })
    }
}