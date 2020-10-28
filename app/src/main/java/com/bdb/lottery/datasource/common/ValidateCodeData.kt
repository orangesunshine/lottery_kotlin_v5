package com.bdb.lottery.datasource.common

//登录失败，下次是否需要验证码
data class ValidateCodeData(
    val needValidateCode: Boolean
)