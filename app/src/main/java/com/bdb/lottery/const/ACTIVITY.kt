package com.bdb.lottery.const

import com.bdb.lottery.biz.lot.activity.LotActivity
import com.bdb.lottery.biz.main.MainActivity

object ACTIVITY {
    //必须登录
    val NEED_LOGIN = arrayOf(MainActivity::class.java)

    //必须登录且不能是试玩
    val NO_TRY_NEED_LOGIN = arrayOf(LotActivity::class.java)
}