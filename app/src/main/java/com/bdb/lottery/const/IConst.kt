package com.bdb.lottery.const

interface IConst {
    companion object {
        var BASE_URL: String = "http://good6789.com"
        val DEFAULT_ERROR_CODE = -1//自定义通用错误
        val DOAMIN_ERROR_CODE = -2//自定义域名获取失败
    }
}

interface ITag {
    companion object {
        val COMMON_LOADING: String = "tag_common_loading"
    }
}

interface ICache {
    companion object {
        val CHACHE_DEVICE_ID = "chache_device_id"
        val CACHE_FRONT_CONFIG = "cache_front_config"
    }
}

interface IDebugConfig {
    companion object {
        val BUGLY_APPID = "5c7c8170b8"
        val BUGLY_CRASH_SCENE_Tag = 172035
        val URL_TEST_DOMAIN = "http://good6789.com"
    }
}