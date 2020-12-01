package com.bdb.lottery.const

interface ICode {
    companion object {
        const val NET_SUCCESSFUL_CODE: Int = 200
        const val DEFAULT_ERROR_CODE = -1//自定义通用错误
        const val DOAMIN_ERROR_CODE = -2//自定义域名获取失败
        const val JSONSYNTAX_ERROR_CODE = -3//解析数据错误

        //region 需要跳转登录
        const val OTHER_DEVICE_LOGINED_CODE = 1111//在其他设备登录
        const val ILLEGAL_TOKEN_CODE = -1003//token非法
        const val EXPIRED_TOKEN_CODE = -1002//token已经过期
        const val NO_LOGINED_CODE = -1001//还没有登录
        const val MAINTAIN_TOKEN_CODE = 1111//在其他设备登录
        const val NEED_LOGIN_CODE = 1111//登录"游客无权限"
        //endregion

        val LIST_TOLOGIN = arrayOf(OTHER_DEVICE_LOGINED_CODE,
            ILLEGAL_TOKEN_CODE,
            EXPIRED_TOKEN_CODE,
            NO_LOGINED_CODE,
            MAINTAIN_TOKEN_CODE,
            NEED_LOGIN_CODE)
    }
}