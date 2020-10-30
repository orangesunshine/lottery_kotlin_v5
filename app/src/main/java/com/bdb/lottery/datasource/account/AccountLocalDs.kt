package com.bdb.lottery.datasource.account

import com.bdb.lottery.const.ICache
import com.bdb.lottery.utils.cache.Cache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountLocalDs @Inject constructor() {
    private var isLogin = false

    //缓存登录状态
    fun saveIsLogin(isLogin: Boolean) {
        this.isLogin = isLogin()
        Cache.putBoolean(ICache.ISLOGIN_CACHE, isLogin)
    }

    //返回用户登录状态
    fun isLogin(): Boolean {
        if (!isLogin) isLogin = Cache.getBoolean(ICache.ISLOGIN_CACHE)
        return isLogin
    }
}