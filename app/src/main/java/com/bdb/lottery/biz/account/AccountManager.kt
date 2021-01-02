package com.bdb.lottery.biz.account

import com.bdb.lottery.datasource.account.data.UserBalanceData
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.utils.cache.TCache
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
        this.isLogin = isLogin() == true
        tCache.cacheLoginStatus(isLogin)
    }

    //返回用户登录状态
    fun isLogin(): Boolean {
        if (!isLogin) isLogin = tCache.loginStatusCache() == true
        return isLogin
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