package com.bdb.lottery.biz.login

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.utils.cache.TCache
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    val tCache: TCache,
    private val accountRemoteDs: AccountRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
    val validateLd = LiveDataWrapper<Boolean>()
    val needValidated = LiveDataWrapper<Boolean>()

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
            tCache.cacheLogin(username, if (rememberPwd) pwd else "", rememberPwd)
        }, { validate ->
            validateLd.setData(validate)
        }, viewState = viewStatus)
    }

    fun cachePriCustomServiceUrl() {
        appRemoteDs.cachePriCustomServiceUrl()
    }

    fun cachePriApkVersion() {
        appRemoteDs.cachePriApkVersion()
    }

    fun trialPlay(success: () -> Unit) {
        accountRemoteDs.trialPlay({
            success()
            userInfo()//获取用户信息
        }, viewStatus)
    }

    fun userInfo() {
        accountRemoteDs.userInfo()
    }

    //进入登录页面是否需要显示验证码
    fun validate() {
        accountRemoteDs.needValidate { it?.let { if (it) validateLd.setData(true) } }
    }

    //刷新公钥
    fun refreshRsaKey() {
        appRemoteDs.platformParams {
            tCache.cacheRsaPublicKey(it?.rsaPublicKey)
        }
    }
}