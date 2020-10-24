package com.bdb.lottery.const

interface IConst {
    companion object {
        var BASE_URL: String = "http://good6789.com"
        var CODE_SUCCESSFUL: Int = 200
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
        val CHACHE_DEVICE_ID = "chache_device_id"//设备号
        val CACHE_PLATEFORM = "cache_plateform"//平台
        val CACHE_DOMAIN_URL = "cache_domain_url"//域名
        val CACHE_PUBLIC_RSA = "cache_public_rsa"//公钥
        val CACHE_CUSTOM_SERVICE_URL = "cache_custom_service_url"//客服url
        val CACHE_APK_VERSION = "cache_apk_version"//apk版本信息
    }
}

interface IDebugConfig {
    companion object {
        val BUGLY_APPID = "5c7c8170b8"
        val BUGLY_CRASH_SCENE_Tag = 172035
        val URL_TEST_DOMAIN = "http://good6789.com"
    }
}