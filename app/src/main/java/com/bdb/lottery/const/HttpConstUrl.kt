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

        //前台配置
        val URL_CONFIG_FRONT = "/system-config/front.mvc"
    }
}