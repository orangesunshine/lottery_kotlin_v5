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
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.const.CONST
import kotlin.reflect.KProperty1

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_LONG) {
    toast(getString(resId), length)
}

fun Context.toast(msg: String?, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, length).show()
}

inline fun <reified T : Activity> Context.start(noinline block: ((Intent) -> Unit)? = null) {
    val intent = Intent(this, T::class.java)
    if (this !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    block?.invoke(intent)
    startActivity(intent)
}

inline fun <reified T : Activity> Context.startNdFinish(noinline block: ((Intent) -> Unit)? = null) {
    start<T>(block)
    if (this is Activity) finish()
}

inline fun <reified T : Activity> Fragment.start(noinline block: ((Intent) -> Unit)? = null) {
    this.activity?.start<T>(block)
}

inline fun <reified T : Activity> Fragment.startNdFinish(noinline block: ((Intent) -> Unit)? = null) {
    this.activity?.startNdFinish<T>(block)
}

inline fun <reified T : Activity> View.start(noinline block: ((Intent) -> Unit)? = null) {
    context.start<T>(block)
}

inline fun <reified T : Activity> View.startNdFinish(noinline block: ((Intent) -> Unit)? = null) {
    context.startNdFinish<T>(block)
}

inline fun <reified T : Activity> Context.startActivity(
    vararg params: Pair<KProperty1<out Any?, Any?>, Any?>,
) {
    start<T> {
        val extras = params.map { it.first.name to it.second }.toTypedArray()
        it.putExtras(bundleOf(*extras))
    }
}

fun BaseActivity<*>.loading(show: Boolean) {
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
    if (this is BaseActivity<*>)
        statusBar?.let {
            (it.layoutParams as ViewGroup.MarginLayoutParams).topMargin = CONST.HEIGHT_STATUS_BAR
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
