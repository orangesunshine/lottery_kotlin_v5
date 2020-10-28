package com.bdb.lottery.const

interface HttpConstUrl {
    companion object {
        /**
         * TXT域名
         */
        val DOMAINS_API_TXT = arrayOf(
            //阿里云
            "https://ythcoceanpark.net/",
            "https://dongfangyinfeng.com/",
            "https://bdbapp.oss-cn-shenzhen.aliyuncs.com/",
            //七牛
            "https://gxtgw.cn/",
            "http://oklznzb.com/",
            "https://o3rjctsi8.qnssl.com/"
        )

        const val URL_CONFIG_FRONT = "/system-config/front.mvc"//前台配置

        const val URL_CUSTOM_SERVICE: String = "/login/kefuxian.mvc"//客服

        const val URL_APK_VERSION: String = "/login/apkversion.mvc"//版本信息

        ///////////////////////////////////////////////////////////////////////////
        // 登录
        ///////////////////////////////////////////////////////////////////////////
        const val URL_LOGIN: String = "/login/login.mvc"//登录

        const val ULR_LOGIN_TRIAL: String = "/login/shiwangAdd.mvc"//试玩

        const val URL_GET_VERIFYCODE = "/verifyCode"//验证码

        const val URL_NEED_VALIDATE_CODE = "/login/need-validate-code.mvc"//首次进入登录是否需要验证码
    }
}