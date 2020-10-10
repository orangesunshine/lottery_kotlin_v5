package com.bdb.lottery.utils

import android.app.Activity
import android.content.Intent

object Activitys{
    inline fun <reified T : Activity> Activity.start() {
        this.startActivity(Intent(this, T::class.java))
    }

    inline fun <reified T : Activity> Activity.startWithArgs(block: (Intent) -> Any) {
        var intent = Intent(this, T::class.java)
        block(intent)
        this.startActivity(intent)
    }
}