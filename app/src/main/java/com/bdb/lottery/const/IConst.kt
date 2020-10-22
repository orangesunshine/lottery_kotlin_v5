package com.bdb.lottery.const

interface IConst {
    companion object {
        var BASE_URL: String = "http://good6789.com"
        val DEFAULT_ERROR_CODE = -1
    }
}

interface ITag {
    companion object {
        val COMMON_LOADING: String = "tag_common_loading"
    }
}

interface ICache {
    companion object {
        val DEVICE_ID = "device_id"
    }
}

interface IDebugConfig {
    companion object {
        val BUGLY_APPID = "5c7c8170b8"
        val BUGLY_CRASH_SCENE_Tag = 172035
        val URL_TEST_DOMAIN = "http://good6789.com"
    }
}