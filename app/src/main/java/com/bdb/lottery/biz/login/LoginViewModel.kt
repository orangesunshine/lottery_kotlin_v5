package com.bdb.lottery.biz.login

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val accountRemoteDs: AccountRemoteDs,
    private val appRemoteDs: AppRemoteDs
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
            Cache.putBoolean(ICache.LOGIN_REMEMBER_PWD_CACHE, rememberPwd)
            Cache.putString(ICache.LOGIN_USERNAME_CACHE, username)
            Cache.putString(ICache.LOGIN_PWD_CACHE, if (rememberPwd) pwd else "")
        }, { code, msg ->
            Cache.putString(ICache.LOGIN_PWD_CACHE, "")
            Cache.putBoolean(ICache.LOGIN_REMEMBER_PWD_CACHE, false)
            Retrofits.msg2Error<Boolean>(
                msg,
                { error?.run { this(it) } },
                { if (null != it && it) needValidated.setData(it) })

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
        appRemoteDs.getPlateformParams {
            context.toast(it?.rsaPublicKey)
            Cache.putString(ICache.PUBLIC_RSA_CACHE, it?.rsaPublicKey)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////
}