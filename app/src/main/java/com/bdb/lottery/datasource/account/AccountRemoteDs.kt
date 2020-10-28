package com.bdb.lottery.datasource.account

import android.content.Context
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IConst
import com.bdb.lottery.utils.Encrypts
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.Retrofits
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Inject

class AccountRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private var retrofit: Retrofit
) {
    val accountApi = retrofit.create(AccountApi::class.java)

    //登录
    fun login(
        username: String,
        pwd: String,
        clientId: String,
        verifycode: String,
        browserInfo: String,
        success: ((String?) -> Any)? = null,
        error: ((code: Int, msg: String?) -> Any)? = null
    ) {

        val params = HashMap<String, Any>()
        params.put("username", username)
        params.put("password", pwd)
        params.put("pushClientId", clientId)
        params.put("validate", verifycode)
        params.put("browserInfo", browserInfo)
        val paramsJson = GsonBuilder().create().toJson(params)
        val key = Cache.getString(ICache.PUBLIC_RSA_CACHE, "")
        if (null == key) {
            error?.let { it(IConst.DEFAULT_ERROR_CODE, "加密失败") }
            return
        }

        val encrypRsa = Encrypts.rsaEncryptPublicKey(paramsJson, key)

        if (null == encrypRsa) {
            error?.let { it(IConst.DEFAULT_ERROR_CODE, "加密失败") }
            return
        }
        Retrofits.observe(
            accountApi.login(encrypRsa),
            success,
            { code, msg ->
                Retrofits.msg2Error<String>(
                    msg,
                    { error?.run { this(code, it) } },
                    { Cache.putString(ICache.PUBLIC_RSA_CACHE, it) })
            }
        )
    }

    //进入登录页面是否需要显示验证码
    fun needValidate(success: (Boolean?) -> Any?) {
        Retrofits.observe(accountApi.needvalidate(), success)
    }

    //试玩
    fun trialPlay() {
        Retrofits.observe(accountApi.trialPlay())
    }
}