package com.bdb.lottery.datasource.account

import com.bdb.lottery.const.ICache
import com.bdb.lottery.utils.cache.TCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountLocalDs @Inject constructor() {
    private var isLogin = false
    @Inject
    lateinit var tCache: TCache

    //缓存登录状态
    fun saveIsLogin(isLogin: Boolean) {
        this.isLogin = isLogin()
        tCache.putBoolean(ICache.ISLOGIN_CACHE, isLogin)
    }

    //返回用户登录状态
    fun isLogin(): Boolean {
        if (!isLogin) isLogin = tCache.getBoolean(ICache.ISLOGIN_CACHE)
        return isLogin
    }
}