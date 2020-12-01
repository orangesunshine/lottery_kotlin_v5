package com.bdb.lottery.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.const.IConst
import com.bdb.lottery.const.IExtra
import timber.log.Timber
import kotlin.reflect.KProperty1

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_LONG) {
    toast(getString(resId), length)
}

fun Context.toast(msg: String?, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, length).show()
}

inline fun <reified T : Activity> Context.start() {
    val intent = Intent(this, T::class.java)
    if (this !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

inline fun <reified T : Activity> Context.startNdFinish() {
    start<T>()
    if (this is Activity) finish()
}

inline fun <reified T : Activity> Context.startWithArgs(block: (Intent) -> Any) {
    val intent = Intent(this, T::class.java)
    block(intent)
    start<T>()
}

inline fun <reified T : Activity> Context.startNdFinishWithArgs(block: (Intent) -> Any) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
    if (this is Activity) finish()
}

inline fun <reified T : Activity> Fragment.start() {
    startActivity(Intent(this.activity, T::class.java))
}

inline fun <reified T : Activity> Fragment.startNdFinish() {
    startActivity(Intent(this.activity, T::class.java))
    if (this is Activity) finish()
}

inline fun <reified T : Activity> Fragment.startWithArgs(block: (Intent) -> Any) {
    val intent = Intent(this.activity, T::class.java)
    block(intent)
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startNdFinishWithArgs(block: (Intent) -> Any) {
    val intent = Intent(this.activity, T::class.java)
    block(intent)
    startActivity(intent)
    if (this is Activity) finish()
}

inline fun <reified T : AppCompatActivity> Context.startActivity(
    vararg params: Pair<KProperty1<out Any?, Any?>, Any?>,
) {
    val extras = params.map { it.first.name to it.second }.toTypedArray()
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*extras))
    startActivity(intent)
}

fun BaseActivity.loading(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}

fun BaseFragment.loading(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}

/**
 * 设置状态栏文字是否黑色
 * @param light:true 黑色，false：白色
 */
fun Activity.statusbar(light: Boolean) {
    if (this is BaseActivity)
        statusBar?.let {
            (it.layoutParams as ViewGroup.MarginLayoutParams).topMargin = IConst.HEIGHT_STATUS_BAR
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0及以上
        window.statusBarColor = if (light) ContextCompat.getColor(
            this,
            R.color.color_status
        ) else Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //4.4到5.0
        window.attributes.flags =
            window.attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //android6.0以后可以对状态栏文字颜色和图标进行修改
        if (light)
            window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or if (light) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

inline fun <reified D> LifecycleOwner.ob(
    data: LiveData<D>?,
    crossinline block: (D) -> Unit,
) {
    data?.let {
        data.observe(this, Observer { block(it) })
    }
}
