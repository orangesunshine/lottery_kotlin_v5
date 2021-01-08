package com.bdb.lottery.utils.cache

import android.text.TextUtils
import com.bdb.lottery.const.CACHE
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.account.data.UserInfoData
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.cocos.data.CocosData
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
        Caches.putString(CACHE.DEVICE_ID_CACHE, uuid)
        return uuid
    }

    //获取设备uuid
    fun cachePriUUid(): String {
        return (Caches.getString(CACHE.DEVICE_ID_CACHE, null)) ?: let {
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

    fun refreshTimeCache(key: String): Long? {
        return Caches.getLong(key)
    }

    fun refreshTimeCache(key: String, mills: Long): Long? {
        return Caches.getLong(key, mills)
    }
    //endregion


    //region 重启app充值缓存
    fun clearCacheOnSplash() {
        Caches.putString(CACHE.DOMAIN_URL_CACHE)//域名
        Caches.putString(URL.URL_PLATFORM_PARAMS)//平台
        Caches.putString(CACHE.PUBLIC_RSA_KEY_CACHE)//公钥
        Caches.putString(URL.URL_CUSTOM_SERVICE)//客服线
        Caches.putString(URL.URL_APK_VERSION)//版本信息
    }
    //endregion

    //region 引导缓存
    fun cacheSplashGuide() {
        Caches.putBoolean(CACHE.GUIDE_CACHE, true)
    }

    fun splashGuideCache(): Boolean? {
        return Caches.getBoolean(CACHE.GUIDE_CACHE, false)
    }
    //endregion

    //region 登录成功后缓存
    fun cacheLogin(account: String, pwd: String, rememberPwd: Boolean) {
        cacheAccount(account)
        cachePwd(pwd)
        cacheRememberPwd(rememberPwd)
    }

    private fun cacheAccount(account: String) {
        Caches.putString(CACHE.LOGIN_ACCOUNT_CACHE, account)
    }

    private fun cachePwd(pwd: String) {
        Caches.putString(CACHE.LOGIN_PWD_CACHE, pwd)
    }

    private fun cacheRememberPwd(rememberPwd: Boolean) {
        Caches.putBoolean(CACHE.LOGIN_REMEMBER_PWD_CACHE, rememberPwd)
    }
    //endregion

    //region 保存公钥
    fun cacheRsaPublicKey(publicKey: String?) {
        if (!publicKey.isSpace()) Caches.putString(CACHE.PUBLIC_RSA_KEY_CACHE, publicKey)
    }

    fun rsaPublicKeyCache(): String? {
        return Caches.getString(CACHE.PUBLIC_RSA_KEY_CACHE)
    }
    //endregion

    //region token
    fun cacheToken(token: String?) {
        if (!token.isSpace()) Caches.putString(CACHE.TOKEN_CACHE, token)
    }

    fun tokenCache(): String? {
        return Caches.getString(CACHE.TOKEN_CACHE)
    }

    fun clearTokenCache(){
        Caches.putString(CACHE.TOKEN_CACHE)
    }
    //endregion

    //region domain
    fun clearDomainCache() {
        cacheDomain(null)
    }

    fun cacheDomain(domain: String?) {
        if (!domain.isSpace()) Caches.putString(CACHE.DOMAIN_URL_CACHE, domain)
    }

    fun domainCache(): String? {
        return Caches.getString(CACHE.DOMAIN_URL_CACHE)
    }
    //endregion

    //region 平台参数
    fun cachePlatformParams(params: PlatformData?) {
        if (null != params)
            Caches.putString(URL.URL_PLATFORM_PARAMS, Gsons.toJson(params))
    }

    fun platformParamsCache(): PlatformData? {
        return Caches.getString(URL.URL_PLATFORM_PARAMS)?.let { Gsons.fromJson<PlatformData>(it) }
    }
    //endregion

    //region 登录状态
    fun cacheLoginStatus(isLogin: Boolean) {
        Caches.putBoolean(CACHE.ISLOGIN_CACHE, isLogin)
    }

    fun loginStatusCache(): Boolean? {
        return Caches.getBoolean(CACHE.ISLOGIN_CACHE)
    }
    //endregion

    //region 用户信息
    fun cacheUserInfo(info: UserInfoData?) {
        if (null != info) Caches.putString(CACHE.USERINFO_CACHE, Gsons.toJson(info))
    }

    fun userInfoCache(): UserInfoData? {
        return Caches.getString(CACHE.USERINFO_CACHE)?.let { Gsons.fromJson<UserInfoData>(it) }
    }
    //endregion

    //region cocos配置缓存
    fun cacheCocosConfig(cocos: CocosData?) {
        if (null != cocos) Caches.putString(CACHE.COCOS_CONFIG_CACHE, Gsons.toJson(cocos))
    }

    fun cocosConfigCache(): CocosData? {
        return Caches.getString(CACHE.COCOS_CONFIG_CACHE)?.let { Gsons.fromJson<CocosData>(it) }
    }
    //endregion

    //region 经典：gameid对应cb选中玩法缓存
    fun cachePlay4GameId(
        gameId: Int, play: Int, group: Int,
        bet: Int, parentPlayId: Int, playId: Int,
    ) {
        val gameIdText = gameId.toString()
        Caches.putInt(gameIdText + CACHE.LOT_JD_PLAY_CACHE, play)
        Caches.putInt(gameIdText + CACHE.LOT_JD_GROUP_CACHE, group)
        Caches.putInt(gameIdText + CACHE.LOT_JD_BET_CACHE, bet)
        Caches.putInt(gameIdText + CACHE.LOT_JD_PARENT_PLAY_ID_CACHE, parentPlayId)
        Caches.putInt(gameIdText + CACHE.LOT_JD_PLAY_ID_CACHE, playId)
    }

    fun playCacheByGameId(gameId: Int): Int? {
        return Caches.getInt(gameId.toString() + CACHE.LOT_JD_PLAY_CACHE, 0)
    }

    fun playGroupCacheByGameId(gameId: Int): Int? {
        return Caches.getInt(gameId.toString() + CACHE.LOT_JD_GROUP_CACHE, 0)
    }

    fun betCacheByGameId(gameId: Int): Int? {
        return Caches.getInt(gameId.toString() + CACHE.LOT_JD_BET_CACHE, 0)
    }

    fun playIdCacheByGameId(gameId: Int): Int? {
        return Caches.getInt(gameId.toString() + CACHE.LOT_JD_PLAY_ID_CACHE, 0)
    }

    fun parentPlayIdCacheByGameId(gameId: Int): Int? {
        return Caches.getInt(gameId.toString() + CACHE.LOT_JD_PARENT_PLAY_ID_CACHE, 0)
    }
    //endregion

    //region lot：金额单位
    fun cacheMoneyUnit(gameId: Int, playId: Int, amountUnit: Int) {
        Caches.putInt("$gameId${CACHE.LOT_JD_MONEY_UNIT_CACHE}$playId", amountUnit)
    }

    fun moneyUnitCache(gameId: Int, playId: Int): Int? {
        return Caches.getInt("$gameId${CACHE.LOT_JD_MONEY_UNIT_CACHE}$playId", 1)
    }
    //endregion
}