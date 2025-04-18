package com.bdb.lottery.datasource.account

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.base.response.errorData
import com.bdb.lottery.biz.globallivedata.AccountManager
import com.bdb.lottery.datasource.account.data.UserBalanceData
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.encrypt.TEncrypt
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AccountRemoteDs @Inject constructor(
    private val tCache: TCache,
    private val appApi: AppApi,
    private val tEncrypt: TEncrypt,
    private val accountApi: AccountApi,
    private val accountManager: AccountManager,
    private val retrofitWrapper: RetrofitWrapper,
) {
    //登录
    fun login(
        username: String,
        pwd: String,
        clientId: String,
        validate: String,
        browserInfo: String,
        success: () -> Unit,
        error: (validate: Boolean) -> Unit,
        viewState: LiveDataWrapper<ViewState?>,
    ) {
        val key = tCache.rsaPublicKeyCache()
        if (key.isSpace()) {
            retrofitWrapper.observeErrorData(
                appApi.platformParams()
                    .flatMap {
                        var observable: Observable<BaseResponse<String>>? = null
                        it.data.rsaPublicKey.let {
                            if (!it.isSpace()) {
                                val params = HashMap<String, Any>()
                                params["username"] = username
                                params["password"] = pwd
                                params["pushClientId"] = clientId
                                params["validate"] = validate
                                params["browserInfo"] = browserInfo
                                val paramsJson = GsonBuilder().create().toJson(params)
                                try {
                                    tEncrypt.rsaEncryptPublicKey(paramsJson, it)
                                        ?.let { observable = accountApi.login(it) }
                                } catch (e: Exception) {
                                }
                            }
                        }
                        observable
                    }, {
                    //缓存用户已登录
                    accountManager.saveIsLogin(true)
                    tCache.cacheToken(it)
                    success()
                },
                { response ->
                    //缓存用户未登录
                    accountManager.saveIsLogin(false)
                    response.errorData<Map<String, Boolean>>()
                        ?.let { error(it.get("needValidateCode") ?: false) }
                }, viewState = viewState
            )
        } else {
            loginReal(
                key!!,
                username,
                pwd,
                clientId,
                validate,
                browserInfo,
                success,
                error,
                viewState
            )
        }
    }

    private fun loginReal(
        key: String,
        username: String,
        pwd: String,
        clientId: String,
        validate: String,
        browserInfo: String,
        success: () -> Unit,
        error: (validate: Boolean) -> Unit,
        viewState: LiveDataWrapper<ViewState?>,
    ) {
        val params = HashMap<String, Any>()
        params["username"] = username
        params["password"] = pwd
        params["pushClientId"] = clientId
        params["validate"] = validate
        params["browserInfo"] = browserInfo
        val paramsJson = GsonBuilder().create().toJson(params)
        try {
            tEncrypt.rsaEncryptPublicKey(paramsJson, key)?.let {
                retrofitWrapper.observeErrorData(
                    accountApi.login(it),
                    {
                        //缓存用户已登录
                        accountManager.saveIsLogin(true)
                        tCache.cacheToken(it)
                        success()
                    },
                    { response ->
                        response.errorData<Map<String, Boolean>>()
                            ?.let { error(it.get("needValidateCode") ?: false) }
                    }, viewState = viewState
                )
            } ?: let { BdbApp.context.toast("加密失败") }

        } catch (e: Exception) {
            BdbApp.context.toast("加密失败")
        }
    }


    //是否需要显示验证码
    fun needValidate(success: (Boolean?) -> Unit) {
        retrofitWrapper.observe(accountApi.needValidate(), success)
    }

    //试玩
    fun trialPlay(success: () -> Unit, viewState: LiveDataWrapper<ViewState?>) {
        retrofitWrapper.observe(accountApi.trialPlay(), {
            success()
            //缓存用户已登录
            accountManager.saveIsLogin(true)
            tCache.cacheToken(it.token)
        }, { _, _ ->
            //缓存用户未登录
            accountManager.saveIsLogin(false)
        }, viewState = viewState)
    }

    //用户信息
    fun userInfo() {
        retrofitWrapper.observe(accountApi.userInfo(), {
            //缓存用户登录信息
            tCache.cacheUserInfo(it)
        })
    }

    //余额
    fun balance(success: (UserBalanceData) -> Unit) {
        retrofitWrapper.observe(accountApi.balance(), success)
    }
}