package com.bdb.lottery.biz.login

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IConst
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.common.ValidateCodeData
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.*
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val accountRemoteDs: AccountRemoteDs
) : ViewModel() {
    val validateLd = LiveDataWraper<Boolean>()
    val needValidated = LiveDataWraper<Boolean>()

    fun login(
        username: String,
        pwd: String,
        rememberPwd: Boolean,
        verifyCode: String,
        error: (String?) -> Any
    ) {
        val pushClientId = ""
        val appVersionCode = BuildConfig.VERSION_NAME //APP版本号
        accountRemoteDs.login(username, pwd, pushClientId, verifyCode, appVersionCode, {
            //登录成功保存用户名、密码、是否记住密码
            Cache.putBoolean(ICache.CACHE_LOGIN_REMEMBER_PWD, rememberPwd)
            Cache.putString(ICache.CACHE_LOGIN_USERNAME, username)
            Cache.putString(ICache.CACHE_LOGIN_PWD, if (rememberPwd) pwd else "")
        }, { code, msg ->
            error?.let { it(msg) }
            Cache.putString(ICache.CACHE_LOGIN_PWD, "")
            Cache.putBoolean(ICache.CACHE_LOGIN_REMEMBER_PWD, false)
            val needValidate = needValidate(msg)
            if (needValidate) validateLd.setData(true)
        })
    }

    fun getCustomService() {

    }

    fun getApkVersion() {

    }

    fun trialPlay() {

    }

    fun userInfo() {

    }

    fun verifyCode() {

    }

    fun validate() {

    }

    fun plateformParasms() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////
    fun needValidate(msg: String?): Boolean {
        msg?.let {
            if (it.contains(IConst.DATA_SPLIT_SYM)) {
                it.split(IConst.DATA_SPLIT_SYM)?.let {
                    if (it.size > 1) {
                        val validate = it[1]
                        if (!validate.isBlank()) {
                            val validate = Gson().fromJson(validate, ValidateCodeData::class.java)
                            return validate?.needValidateCode ?: false
                        }
                    }
                }
            }
        }
        return false
    }
}