package com.bdb.lottery.const

import android.content.res.Resources

interface IConst {
    companion object {
        const val BASE_URL: String = "http://good6789.com"
        var HEIGHT_STATUS_BAR = Resources.getSystem().getDimensionPixelSize(
            Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        )
    }
}

interface ICode {
    companion object {
        const val NET_SUCCESSFUL_CODE: Int = 200
        const val DEFAULT_ERROR_CODE = -1//自定义通用错误
        const val DOAMIN_ERROR_CODE = -2//自定义域名获取失败
        const val JSONSYNTAX_ERROR_CODE = -3//解析数据错误
    }
}

interface ITag {
    companion object {
        val COMMON_LOADING_TAG: String = "COMMON_LOADING_TAG"
        val HOME_FRAGMENT_TAG: String = "HOME_FRAGMENT_TAG"
        val PROMOTION_FRAGMENT_TAG: String = "PROMOTION_FRAGMENT_TAG"
        val FIND_FRAGMENT_TAG: String = "FIND_FRAGMENT_TAG"
        val USER_FRAGMENT_TAG: String = "USER_FRAGMENT_TAG"
    }
}

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
        val CUSTOM_SERVICE_URL_CACHE = "CUSTOM_SERVICE_URL_CACHE"//客服url
        val APK_VERSION_CACHE = "APK_VERSION_CACHE"//apk版本信息
        val DEVICE_ID_CACHE = "DEVICE_ID_CACHE"//设备号
        val PLATEFORM_CACHE = "PLATEFORM_CACHE"//平台
        val PUBLIC_RSA_CACHE = "PUBLIC_RSA_CACHE"//公钥

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

interface IDebugConfig {
    companion object {
        val BUGLY_APPID = "5c7c8170b8"
        val BUGLY_CRASH_SCENE_TAG = 172035
        val URL_TEST_DOMAIN = "http://good6789.com"
    }
}

interface IAnim {
    companion object {
        /**
         * 登录加载中
         */
        const val LOGIN_LOADING = "animation/login/loading.json"

        /**
         * 欢迎加载中
         */
        const val SPLASH_LOADING = "animation/splash/loading.json"

        /**
         * 通用加载中
         */
        const val LOADING_LOADING = "animation/loading/loading.json"

        /**
         * 开奖动画cocos配置文件
         */
        const val COCOS_CONFIG =
            "@https://bdbapp.oss-cn-shenzhen.aliyuncs.com/lottery/common/animation/draw/json/data.json" +
                    "@https://dongfangyinfeng.com/lottery/common/animation/draw/json/data.json" +
                    "@https://gxtgw.cn/lottery/common/animation/draw/json/data.json" +
                    "@http://oklznzb.com/lottery/common/animation/draw/json/data.json" +
                    "@https://o3rjctsi8.qnssl.com/lottery/common/animation/draw/json/data.json"
    }
}