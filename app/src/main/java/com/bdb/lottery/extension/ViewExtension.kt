package com.bdb.lottery.extension

import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.anim.ClickAnimator
import com.bdb.lottery.utils.ui.view.Views
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout

val appearAnim = AlphaAnimation(0f, 1f).apply { duration = 1000 }
val disappearAnim = AlphaAnimation(1f, 0f).apply { duration = 1000 }

fun ViewStub.attach(@LayoutRes layoutId: Int): View {
    layoutResource = layoutId
    return inflate()
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.select(select: Boolean) {
    isSelected = select
}

fun View.alphaVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
    startAnimation(if (visible) appearAnim else disappearAnim)
}

fun ImageView?.loadImageUrl(url: String?) {
    this?.let {
        Glide.with(this).load(url).into(this)
    }
}

fun ImageView?.loadImageUrl(glideUrl: GlideUrl) {
    this?.let {
        Glide.with(this).load(glideUrl).into(this)
    }
}

fun <T> RecyclerView.setListOrUpdate(
    list: List<T>?,
    create: (List<T>?) -> BaseQuickAdapter<T, BaseViewHolder>,
) {
    val adapter = this.adapter
    if (null == adapter) this.adapter =
        create(list) else (adapter as BaseQuickAdapter<T, BaseViewHolder>).setList(list)
}

fun View.margin(
    left: Int = Int.MAX_VALUE,
    top: Int = Int.MAX_VALUE,
    right: Int = Int.MAX_VALUE,
    bottom: Int = Int.MAX_VALUE,
) {
    val lp = layoutParams
    if (null != lp && lp is ViewGroup.MarginLayoutParams) {
        lp.setMargins(
            if (left == Int.MAX_VALUE) lp.leftMargin else left,
            if (top == Int.MAX_VALUE) lp.topMargin else top,
            if (right == Int.MAX_VALUE) lp.rightMargin else right,
            if (bottom == Int.MAX_VALUE) lp.bottomMargin else bottom
        )
    }
}

//region smartrefreshlayout 惯性问题
val SmartRefreshLayout.scrollListener: RecyclerView.OnScrollListener
    get() =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                setEnableRefresh(!recyclerView.canScrollVertically(-1))
            }
        }


fun SmartRefreshLayout?.bindNoFlingRecyclerView(recyclerView: RecyclerView?) {
    this?.let {
        recyclerView?.addOnScrollListener(it.scrollListener)
    }
}

fun SmartRefreshLayout?.unbindNoFlingRecyclerView(recyclerView: RecyclerView?) {
    this?.let {
        recyclerView?.removeOnScrollListener(it.scrollListener)
    }
}
//endregion

fun TabLayout.Tab?.updateTab(selected: Boolean) {
    this?.let {
        val tabView = it.customView as TextView
        Views.setTextAppearance(
            tabView,
            if (selected) R.style.TabLayoutTextSelected else R.style.TabLayoutTextUnSelected
        )
    }
}

fun TextView.setDpTextSize(size:Float){
    setTextSize(TypedValue.COMPLEX_UNIT_DIP,size)
}

//防抖动，点击特效
fun View?.deBounce(duration: Int = 1000, listener: View.OnClickListener) {
    this?.let {
        this.setOnClickListener(listener)
        this.setOnTouchListener(View.OnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    val tag = v.getTag(R.id.tag_shake_id)
                    val elapsedRealtime = SystemClock.elapsedRealtime()
                    if (null != tag && tag is Long && elapsedRealtime - tag < duration) {
                        true
                    } else {
                        it.clickEffect()
                        v.setTag(R.id.tag_shake_id, elapsedRealtime)
                        false
                    }
                }
            }
            false
        })
    }
}

fun View?.clickEffect() {
    if (null == this) return
    YoYo.with(ClickAnimator())
        .duration(150)
        .playOn(this);
}