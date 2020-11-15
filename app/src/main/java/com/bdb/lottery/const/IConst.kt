package com.bdb.lottery.const

import android.content.res.Resources

interface IConst {
    companion object {
        const val BASE_URL: String = "http://good6789.com"
        var HEIGHT_STATUS_BAR = Resources.getSystem().getDimensionPixelSize(
            Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        )
    }
}