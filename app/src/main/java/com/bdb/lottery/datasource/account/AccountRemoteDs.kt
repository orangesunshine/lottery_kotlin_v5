package com.bdb.lottery.datasource.account

import android.content.Context
import android.util.Base64
import android.util.Base64.DEFAULT
import com.bdb.lottery.const.ICache
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.Retrofits
import com.google.gson.Gson
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
        val toJson = GsonBuilder().create().toJson(params)
        val rsa = Cache.getString(ICache.CACHE_PUBLIC_RSA, "")
        val encrypRsa = Apps.encrypRsa(Base64.decode(toJson, DEFAULT), Base64.decode(rsa, DEFAULT))

        Retrofits.observe(
            accountApi.login(Base64.encodeToString(encrypRsa, DEFAULT)),
            success,
            error
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