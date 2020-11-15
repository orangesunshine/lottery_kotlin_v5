package com.bdb.lottery.const

interface ICode {
    companion object {
        const val NET_SUCCESSFUL_CODE: Int = 200
        const val DEFAULT_ERROR_CODE = -1//自定义通用错误
        const val DOAMIN_ERROR_CODE = -2//自定义域名获取失败
        const val JSONSYNTAX_ERROR_CODE = -3//解析数据错误
    }
}