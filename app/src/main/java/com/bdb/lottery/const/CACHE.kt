package com.bdb.lottery.const

object CACHE {
    ///////////////////////////////////////////////////////////////////////////
    // splash
    ///////////////////////////////////////////////////////////////////////////
    const val GUIDE_CACHE = "GUIDE_CACHE"//splash引导

    ///////////////////////////////////////////////////////////////////////////
    // app通用
    ///////////////////////////////////////////////////////////////////////////
    const val DOMAIN_URL_CACHE = "DOMAIN_URL_CACHE"//域名
    const val DEVICE_ID_CACHE = "DEVICE_ID_CACHE"//设备号
    const val PUBLIC_RSA_KEY_CACHE = "PUBLIC_RSA_KEY_CACHE"//公钥
    const val USER_BALANCE_CACHE = "USER_BALANCE_CACHE"//用户余额
//        val CUSTOM_SERVICE_URL_CACHE = "CUSTOM_SERVICE_URL_CACHE"//客服url
//        val APK_VERSION_CACHE = "APK_VERSION_CACHE"//apk版本信息
//        val PLATEFORM_CACHE = "PLATEFORM_CACHE"//平台

    ///////////////////////////////////////////////////////////////////////////
    // login
    ///////////////////////////////////////////////////////////////////////////
    const val LOGIN_REMEMBER_PWD_CACHE = "LOGIN_REMEMBER_PWD_CACHE"//是否记住密码
    const val LOGIN_ACCOUNT_CACHE = "LOGIN_ACCOUNT_CACHE"//用户名
    const val LOGIN_PWD_CACHE = "LOGIN_PWD_CACHE"//密码
    const val TOKEN_CACHE = "TOKEN_CACHE"//token
    const val ISLOGIN_CACHE = "ISLOGIN_CACHE"//是否登录
    const val USERINFO_CACHE = "USERINFO_CACHE"//用户信息

    ///////////////////////////////////////////////////////////////////////////
    // 下拉刷新
    ///////////////////////////////////////////////////////////////////////////
    const val LAST_UPDATE_TIME_CACHE = "LAST_UPDATE_TIME_CACHE"//refresh上次更新时间

    ///////////////////////////////////////////////////////////////////////////
    // cocos配置
    ///////////////////////////////////////////////////////////////////////////
    const val COCOS_CONFIG_CACHE = "COCOS_CONFIG_CACHE"//cocos配置

    ///////////////////////////////////////////////////////////////////////////
    // 玩法
    ///////////////////////////////////////////////////////////////////////////
    const val LOT_JD_PLAY_POS_CACHE = "LOT_JD_PLAY_POS_CACHE"//经典玩法第一层(五星、龙虎)
    const val LOT_JD_GROUP_POS_CACHE = "LOT_JD_GROUP_POS_CACHE"//直选、组选
    const val LOT_JD_BET_POS_CACHE = "LOT_JD_BET_POS_CACHE"//经典玩法第二层(直选单式、组选20)
    const val LOT_JD_PLAY_ID_CACHE = "LOT_JD_PLAY_ID_CACHE"//玩法id
    const val LOT_JD_PARENT_PLAY_ID_CACHE = "LOT_JD_PARENT_PLAY_ID_CACHE"//计算形态

    const val LOT_JD_MONEY_UNIT_CACHE = "LOT_JD_MONEY_UNIT_CACHE"//投注单位
}