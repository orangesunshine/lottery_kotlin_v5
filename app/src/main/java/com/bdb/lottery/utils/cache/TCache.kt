package com.bdb.lottery.utils.cache

import android.text.TextUtils
import com.bdb.lottery.const.ICache
import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.account.data.UserInfoData
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.device.Devices
import com.bdb.lottery.utils.gson.Gsons
import javax.inject.Inject

/**
 * 统一缓存方法，mmkv可以替换
 */
class TCache @Inject constructor() {
    //region uuid
    //缓存uuid
    private fun cacheUUid(prifex: Int, id: String): String {
        val uuid = Devices.getUUid(prifex, id)
        Caches.putString(ICache.DEVICE_ID_CACHE, uuid)
        return uuid
    }

    //获取设备uuid
    fun cachePriUUid(): String {
        return (Caches.getString(ICache.DEVICE_ID_CACHE, null)) ?: let {
            val androidId = Devices.getAndroidId()
            cacheUUid(if (TextUtils.isEmpty(androidId)) 9 else 2, androidId)
        }
    }
    //endregion

    //region 下拉时间
    //缓存下拉刷新时间
    fun cacheRefreshTime(key: String) {
        Caches.putLong(key, System.currentTimeMillis())
    }

    fun cacheRefreshTime(key: String, mills: Long) {
        Caches.putLong(key, mills)
    }

    fun refreshTimeCache(key: String): Long {
        return Caches.getLong(key)
    }

    fun refreshTimeCache(key: String, mills: Long): Long {
        return Caches.getLong(key, mills)
    }
    //endregion


    //region 重启app充值缓存
    fun clearCacheOnSplash() {
        Caches.putString(ICache.DOMAIN_URL_CACHE)//域名
        Caches.putString(IUrl.URL_PLATFORM_PARAMS)//平台
        Caches.putString(ICache.PUBLIC_RSA_KEY_CACHE)//公钥
        Caches.putString(IUrl.URL_CUSTOM_SERVICE)//客服线
        Caches.putString(IUrl.URL_APK_VERSION)//版本信息
    }
    //endregion

    //region 引导缓存
    fun cacheSplashGuide() {
        Caches.putBoolean(ICache.GUIDE_CACHE, true)
    }

    fun splashGuideCache(): Boolean {
        return Caches.getBoolean(ICache.GUIDE_CACHE, false)
    }
    //endregion

    //region 登录成功后缓存
    fun cacheLogin(account: String, pwd: String, rememberPwd: Boolean) {
        cacheAccount(account)
        cachePwd(pwd)
        cacheRememberPwd(rememberPwd)
    }

    private fun cacheAccount(account: String) {
        Caches.putString(ICache.LOGIN_ACCOUNT_CACHE, account)
    }

    private fun cachePwd(pwd: String) {
        Caches.putString(ICache.LOGIN_PWD_CACHE, pwd)
    }

    private fun cacheRememberPwd(rememberPwd: Boolean) {
        Caches.putBoolean(ICache.LOGIN_REMEMBER_PWD_CACHE, rememberPwd)
    }
    //endregion

    //region 保存公钥
    fun cacheRsaPublicKey(publicKey: String?) {
        if (!publicKey.isSpace())
            Caches.putString(ICache.PUBLIC_RSA_KEY_CACHE, publicKey)
    }

    fun rsaPublicKeyCache(): String? {
        return Caches.getString(ICache.PUBLIC_RSA_KEY_CACHE)
    }
    //endregion

    //region token
    fun cacheToken(token: String?) {
        if (!token.isSpace())
            Caches.putString(ICache.TOKEN_CACHE, token)
    }

    fun tokenCache(): String? {
        return Caches.getString(ICache.TOKEN_CACHE)
    }
    //endregion

    //region domain
    fun clearDomainCache() {
        cacheDomain(null)
    }

    fun cacheDomain(domain: String?) {
        if (!domain.isSpace())
            Caches.putString(ICache.DOMAIN_URL_CACHE, domain)
    }

    fun domainCache(): String? {
        return Caches.getString(ICache.DOMAIN_URL_CACHE)
    }
    //endregion

    //region 平台参数
    fun cachePlatformParams(params: PlatformData?) {
        if (null != params)
            Caches.putString(IUrl.URL_PLATFORM_PARAMS, Gsons.toJson(params))
    }
    //endregion

    //region 登录状态
    fun cacheLoginStatus(isLogin: Boolean) {
        Caches.putBoolean(ICache.ISLOGIN_CACHE, isLogin)
    }

    fun loginStatusCache(): Boolean {
        return Caches.getBoolean(ICache.ISLOGIN_CACHE)
    }
    //endregion

    //region 用户信息
    fun cacheUserInfo(info: UserInfoData?) {
        if (null != info)
            Caches.putString(ICache.USERINFO_CACHE, Gsons.toJson(info))
    }

    fun userInfoCache(): String? {
        return Caches.getString(ICache.USERINFO_CACHE)
    }
    //endregion
}