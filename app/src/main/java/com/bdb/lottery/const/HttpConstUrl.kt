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

        const val URL_PUSH_CONFIG = "/system-config/message/push-config.mvc"//推送配置

        const val URL_CUSTOM_SERVICE: String = "/login/kefuxian.mvc"//客服

        const val URL_APK_VERSION: String = "/login/apkversion.mvc"//版本信息


        ///////////////////////////////////////////////////////////////////////////
        // 登录
        ///////////////////////////////////////////////////////////////////////////
        const val URL_LOGIN: String = "/login/login.mvc"//登录

        const val ULR_LOGIN_TRIAL: String = "/login/shiwangAdd.mvc"//试玩

        const val URL_VERIFYCODE = "/verifyCode"//验证码

        const val URL_NEED_VALIDATE_CODE = "/login/need-validate-code.mvc"//首次进入登录是否需要验证码

        ///////////////////////////////////////////////////////////////////////////
        // 账户
        ///////////////////////////////////////////////////////////////////////////
        const val ULR_BALANCE = "/userInfo/getBalance.mvc"//余额

        const val URL_USERINFO = "/userInfo/simple-info.mvc"//用户信息

        ///////////////////////////////////////////////////////////////////////////
        // 注册
        ///////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////
        // 首页home
        ///////////////////////////////////////////////////////////////////////////
        const val URL_HOME_BANNER = "/Uploadimg/list.mvc"//轮播图

        const val URL_NOTICE = "/notice/notice.mvc"//通知

        ///////////////////////////////////////////////////////////////////////////
        // 游戏
        ///////////////////////////////////////////////////////////////////////////
        const val URL_THIRD_PLATFORM = "/thirdPartyBetRecords/getThirdPartyPlatform.mvc"//三方游戏列表

        const val URL_OTHER_PLATFORM = "/thirdUserAdd/getPlatform.mvc"//其他平台游戏列表

        const val URL_INIT_GAME = "/gameType/initGameType.mvc"//init

        const val URL_ALL_GAME = "/gameType/getAllGame.mvc"//all game

        const val URL_GAME_BY_GENRES = "/gameType/getGameByGenres.mvc"//获取彩种大类每个才会总信息

        const val URL_LOTTERY_FAVORITES =
            "/userInfo/lottery/favorites/getLotteryFavorites.mvc"//收藏彩种
    }
}