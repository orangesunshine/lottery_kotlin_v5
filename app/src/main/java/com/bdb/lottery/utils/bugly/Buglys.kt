package com.bdb.lottery.utils.bugly

import com.bdb.lottery.app.LotteryApp
import com.bdb.lottery.const.IDebugConfig.Companion.BUGLY_CRASH_SCENE_Tag
import com.tencent.bugly.crashreport.CrashReport

object Buglys {
    fun nullPointReport(msg: String) {
        CrashReport.setUserSceneTag(LotteryApp.context, BUGLY_CRASH_SCENE_Tag)
        CrashReport.postCatchedException(NullPointerException(msg))
    }
}