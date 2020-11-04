package com.bdb.lottery.biz.login

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.utils.cache.Cache
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
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
        success: () -> Unit
    ) {
        val pushClientId = ""
        val appVersionCode = BuildConfig.VERSION_NAME //APP版本号
        accountRemoteDs.login(username, pwd, pushClientId, verifyCode, appVersionCode, {
            success()
            userInfo()//获取用户信息
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

    fun trialPlay(success: () -> Unit) {
        accountRemoteDs.trialPlay({
            success()
            userInfo()//获取用户信息
        }, viewStatus)
    }

    fun userInfo() {
        accountRemoteDs.loginInfo()
    }

    //进入登录页面是否需要显示验证码
    fun validate() {
        accountRemoteDs.needValidate { it?.let { if (it) validateLd.setData(true) } }
    }

    fun platformParams() {
        appRemoteDs.getPlatformParams {
            Cache.putString(ICache.PUBLIC_RSA_KEY_CACHE, it?.rsaPublicKey)
        }
    }
}