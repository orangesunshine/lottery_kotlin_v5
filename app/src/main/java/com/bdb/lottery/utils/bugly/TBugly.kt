package com.bdb.lottery.utils.bugly

import android.content.Context
import com.bdb.lottery.const.DEBUGCONFIG
import com.tencent.bugly.crashreport.CrashReport
import dagger.hilt.android.qualifiers.ApplicationContext

object TBugly {
    fun nullPointReport(@ApplicationContext context: Context, msg: String) {
        CrashReport.setUserSceneTag(context, DEBUGCONFIG.BUGLY_CRASH_SCENE_TAG)
        CrashReport.postCatchedException(NullPointerException(msg))
    }
}