package com.bdb.lottery.datasource.account

import android.content.Context
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.account.data.VerifycodeData
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.Encrypts
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.ApiException
import com.bdb.lottery.utils.net.retrofit.Retrofits
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class AccountRemoteDs @Inject constructor(
    @ActivityContext private val context: Context,
    private val accountApi: AccountApi
) {

    //登录
    fun login(
        username: String,
        pwd: String,
        clientId: String,
        verifycode: String,
        browserInfo: String,
        success: ((String?) -> Any?)? = null,
        error: (validate: Boolean) -> Any?
    ) {
        val params = HashMap<String, Any>()
        params.put("username", username)
        params.put("password", pwd)
        params.put("pushClientId", clientId)
        params.put("validate", verifycode)
        params.put("browserInfo", browserInfo)
        val paramsJson = GsonBuilder().create().toJson(params)
        val key = Cache.getString(ICache.PUBLIC_RSA_CACHE, "")
        try {
            key?.let {
                Encrypts.rsaEncryptPublicKey(paramsJson, key)?.let {
                    Retrofits.observeErrorData(
                        accountApi.login(it),
                        success,
                        { response ->
                            try {
                                response.data?.let {
                                    error?.run {

                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                    )
                } ?: let { context.toast("加密失败") }
            } ?: let { context.toast("加密失败") }

        } catch (e: Exception) {
            context.toast("加密失败")
        }
    }

    //进入登录页面是否需要显示验证码
    fun needValidate(success: (Boolean?) -> Any?) {
        Retrofits.observeErrorData(accountApi.needvalidate(), success)
    }

    //试玩
    fun trialPlay() {
        Retrofits.observe(accountApi.trialPlay())
    }
}