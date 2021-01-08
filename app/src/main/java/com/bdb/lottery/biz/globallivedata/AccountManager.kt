package com.bdb.lottery.biz.globallivedata

import com.bdb.lottery.const.CACHE
import com.bdb.lottery.datasource.account.data.UserBalanceData
import com.bdb.lottery.datasource.account.data.UserInfoData
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.utils.cache.Caches
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.gson.Gsons
import javax.inject.Inject
import javax.inject.Singleton

//管理用户登录状态，余额，经典输赢
@Singleton
class AccountManager @Inject constructor() {
    @Inject
    lateinit var tCache: TCache

    //region 登录状态
    private var isLogin = false

    //缓存登录状态
    fun saveIsLogin(isLogin: Boolean) {
        this.isLogin = isLogin
        tCache.cacheLoginStatus(isLogin)
    }

    //返回用户登录状态
    fun isLogin(): Boolean {
        if (!isLogin) isLogin = tCache.loginStatusCache() == true
        return isLogin
    }
    //endregion

    //region 是否试玩
    fun isShiWan(): Boolean {
        return isLogin && getUserInfo()?.isShiWan ?: false
    }
    //endregion

    //region 用户登录信息
    private var mUserInfo: UserInfoData? = null
    fun saveUserInfo(userInfo: UserInfoData?) {
        this.mUserInfo = userInfo
        tCache.cacheUserInfo(userInfo)
    }

    fun getUserInfo(): UserInfoData? {
        val login = isLogin()
        if (login) {
            if (null == mUserInfo) mUserInfo = tCache.userInfoCache()
        } else {
            Caches.putString(CACHE.USERINFO_CACHE)
            mUserInfo = null
        }
        return mUserInfo
    }
    //endregion

    //region 余额
    var mUserBalance = LiveDataWrapper<UserBalanceData?>()

    //保存用户余额
    fun saveUserBalance(userBalanceData: UserBalanceData?) {
        mUserBalance.setData(userBalanceData)
    }
    //endregion


}