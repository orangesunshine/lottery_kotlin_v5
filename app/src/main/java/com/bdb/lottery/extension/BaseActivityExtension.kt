package com.bdb.lottery.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.const.ITAG
import kotlin.reflect.KProperty1

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(resId))
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

inline fun <reified T : Activity> Activity.start() {
    this.startActivity(Intent(this, T::class.java))
}

inline fun <reified T : Activity> Activity.startWithArgs(block: (Intent) -> Any) {
    var intent = Intent(this, T::class.java)
    block(intent)
    this.startActivity(intent)
}

inline fun <reified T : AppCompatActivity> Context.startActivity(
    vararg params: Pair<KProperty1<out Any?, Any?>, Any?>
) {
    val extras = params.map { it.first.name to it.second }.toTypedArray()
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*extras))
    startActivity(intent)
}

fun BaseActivity.loading() {
    loading.show(supportFragmentManager, ITAG.COMMON_LOADING)
}

fun BaseActivity.hiding() {
    loading.dismissAllowingStateLoss()
}