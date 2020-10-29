package com.bdb.lottery.datasource.account.data

//登录失败，下次是否需要验证码
data class VerifycodeData(
    val needValidateCode: Boolean
)