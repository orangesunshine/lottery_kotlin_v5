package com.bdb.lottery.utils.bugly

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.IDebugConfig.Companion.BUGLY_CRASH_SCENE_TAG
import com.tencent.bugly.crashreport.CrashReport

object Buglys {
    fun nullPointReport(msg: String) {
        CrashReport.setUserSceneTag(BdbApp.context, BUGLY_CRASH_SCENE_TAG)
        CrashReport.postCatchedException(NullPointerException(msg))
    }
}