package com.bdb.lottery.const

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