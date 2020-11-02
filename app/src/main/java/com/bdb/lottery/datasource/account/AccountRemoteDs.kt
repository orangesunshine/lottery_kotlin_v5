package com.bdb.lottery.datasource.account

import android.content.Context
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.base.response.errorData
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.BaseRemoteDs
import com.bdb.lottery.datasource.account.data.BalanceData
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.Encrypts
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AccountRemoteDs @Inject constructor(
    @ActivityContext private val context: Context,
    private val accountApi: AccountApi,
    private val appApi: AppApi,
    private val domainLocalDs: DomainLocalDs,
    private val accountLocalDs: AccountLocalDs
) : BaseRemoteDs() {

    //登录
    fun login(
        username: String,
        pwd: String,
        clientId: String,
        verifycode: String,
        browserInfo: String,
        success: () -> Unit,
        error: (validate: Boolean) -> Unit,
        viewState: LiveDataWraper<ViewState>
    ) {
        val key = Cache.getString(ICache.PUBLIC_RSA_CACHE, "")
        if (key.isSpace()) {
            retrofitWrapper.observeErrorData(
                appApi.plateformParams(domainLocalDs.getDomain() + HttpConstUrl.URL_CONFIG_FRONT)
                    .flatMap {
                        var observable: Observable<BaseResponse<String>>? = null
                        it.data?.rsaPublicKey?.let {
                            if (!it.isSpace()) {
                                val params = HashMap<String, Any>()
                                params.put("username", username)
                                params.put("password", pwd)
                                params.put("pushClientId", clientId)
                                params.put("validate", verifycode)
                                params.put("browserInfo", browserInfo)
                                val paramsJson = GsonBuilder().create().toJson(params)
                                try {
                                    Encrypts.rsaEncryptPublicKey(paramsJson, it)
                                        ?.let { observable = accountApi.login(it) }
                                } catch (e: Exception) {
                                }
                            }
                        }
                        observable
                    }, {
                    //缓存用户已登录
                    accountLocalDs.saveIsLogin(true)
                    it?.let {
                        if (!it.isBlank()) {
                            Cache.putString(ICache.TOKEN_CACHE, it)
                        }
                    }
                    success()
                },
                { response ->
                    //缓存用户未登录
                    accountLocalDs.saveIsLogin(false)
                    response.errorData<Map<String, Boolean>>()
                        ?.let { error(it.get("needValidateCode") ?: false) }
                }, viewState = viewState
            )
        } else {
            loginReal(
                key,
                username,
                pwd,
                clientId,
                verifycode,
                browserInfo,
                success,
                error,
                viewState
            )
        }

    }

    fun loginReal(
        key: String,
        username: String,
        pwd: String,
        clientId: String,
        verifycode: String,
        browserInfo: String,
        success: () -> Unit,
        error: (validate: Boolean) -> Unit,
        viewState: LiveDataWraper<ViewState>
    ) {
        val params = HashMap<String, Any>()
        params.put("username", username)
        params.put("password", pwd)
        params.put("pushClientId", clientId)
        params.put("validate", verifycode)
        params.put("browserInfo", browserInfo)
        val paramsJson = GsonBuilder().create().toJson(params)
        try {
            Encrypts.rsaEncryptPublicKey(paramsJson, key)?.let {
                retrofitWrapper.observeErrorData(
                    accountApi.login(it),
                    {
                        //缓存用户已登录
                        accountLocalDs.saveIsLogin(true)
                        it?.let {
                            if (!it.isBlank()) {
                                Cache.putString(ICache.TOKEN_CACHE, it)
                            }
                        }
                        success()
                    },
                    { response ->
                        response.errorData<Map<String, Boolean>>()
                            ?.let { error(it.get("needValidateCode") ?: false) }
                    }, viewState = viewState
                )
            } ?: let { context.toast("加密失败") }

        } catch (e: Exception) {
            context.toast("加密失败")
        }
    }


    //是否需要显示验证码
    fun needValidate(success: (Boolean?) -> Unit) {
        retrofitWrapper.observe(accountApi.needvalidate(), success)
    }

    //试玩
    fun trialPlay(success: () -> Unit, viewState: LiveDataWraper<ViewState>) {
        retrofitWrapper.observe(accountApi.trialPlay(), {
            success()
            //缓存用户已登录
            accountLocalDs.saveIsLogin(true)
            it?.let {
                val token = it.token
                if (!token.isSpace()) Cache.putString(ICache.TOKEN_CACHE, token)
            }
        }, { code, msg ->
            //缓存用户未登录
            accountLocalDs.saveIsLogin(false)
        }, viewState = viewState)
    }

    //用户信息
    fun loginInfo() {
        retrofitWrapper.observe(accountApi.userinfo(), {
            //缓存用户登录信息
            it?.let { Cache.putString(ICache.USERINFO_CACHE, GsonBuilder().create().toJson(it)) }
        })
    }

    //余额
    fun balance(success: (BalanceData?) -> Unit) {
        retrofitWrapper.observe(accountApi.balance(), success)
    }
}