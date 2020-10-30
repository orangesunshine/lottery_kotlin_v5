package com.bdb.lottery.biz.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.utils.cache.Cache
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    private val accountRemoteDs: AccountRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
    val validateLd = LiveDataWraper<Boolean>()
    val needValidated = LiveDataWraper<Boolean>()

    fun login(
        username: String,
        pwd: String,
        rememberPwd: Boolean,
        verifyCode: String,
        success: () -> Any?
    ) {
        val pushClientId = ""
        val appVersionCode = BuildConfig.VERSION_NAME //APP版本号
        accountRemoteDs.login(username, pwd, pushClientId, verifyCode, appVersionCode, {
            success()
            //登录成功保存用户名、密码、是否记住密码
            Cache.putBoolean(ICache.LOGIN_REMEMBER_PWD_CACHE, rememberPwd)
            Cache.putString(ICache.LOGIN_USERNAME_CACHE, username)
            Cache.putString(ICache.LOGIN_PWD_CACHE, if (rememberPwd) pwd else "")
        }, { validate ->
            validateLd.setData(validate)
        }, viewState = viewStatus)
    }

    fun getCustomService() {
        appRemoteDs.getAPkVeresion()
    }

    fun getApkVersion() {
        appRemoteDs.getAPkVeresion()
    }

    fun trialPlay(success: () -> Any?) {
        accountRemoteDs.trialPlay(success, viewStatus)
    }

    fun userInfo() {
    }

    //进入登录页面是否需要显示验证码
    fun validate() {
        accountRemoteDs.needValidate { it?.let { if (it) validateLd.setData(true) } }
    }

    fun plateformParasms() {
        appRemoteDs.getPlateformParams {
            Cache.putString(ICache.PUBLIC_RSA_CACHE, it?.rsaPublicKey)
        }
    }
}