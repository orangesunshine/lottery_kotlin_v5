package com.bdb.lottery.const

interface ICache {
    companion object {
        ///////////////////////////////////////////////////////////////////////////
        // splash
        ///////////////////////////////////////////////////////////////////////////
        val GUIDE_CACHE = "GUIDE_CACHE"//splash引导

        ///////////////////////////////////////////////////////////////////////////
        // app通用
        ///////////////////////////////////////////////////////////////////////////
        val DOMAIN_URL_CACHE = "DOMAIN_URL_CACHE"//域名
        val DEVICE_ID_CACHE = "DEVICE_ID_CACHE"//设备号
        val PUBLIC_RSA_KEY_CACHE = "PUBLIC_RSA_KEY_CACHE"//公钥
//        val CUSTOM_SERVICE_URL_CACHE = "CUSTOM_SERVICE_URL_CACHE"//客服url
//        val APK_VERSION_CACHE = "APK_VERSION_CACHE"//apk版本信息
//        val PLATEFORM_CACHE = "PLATEFORM_CACHE"//平台

        ///////////////////////////////////////////////////////////////////////////
        // login
        ///////////////////////////////////////////////////////////////////////////
        val LOGIN_REMEMBER_PWD_CACHE = "LOGIN_REMEMBER_PWD_CACHE"//是否记住密码
        val LOGIN_USERNAME_CACHE = "LOGIN_USERNAME_CACHE"//用户名
        val LOGIN_PWD_CACHE = "LOGIN_PWD_CACHE"//密码
        val TOKEN_CACHE = "TOKEN_CACHE"//token
        val ISLOGIN_CACHE = "ISLOGIN_CACHE"//是否登录
        val USERINFO_CACHE = "USERINFO_CACHE"//用户信息

        ///////////////////////////////////////////////////////////////////////////
        // 下拉刷新
        ///////////////////////////////////////////////////////////////////////////
        val LAST_UPDATE_TIME_CACHE = "LAST_UPDATE_TIME_CACHE"//refresh上次更新时间
    }
}