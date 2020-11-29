package com.bdb.lottery.extension

import android.view.View
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import timber.log.Timber

val apperAnim = AlphaAnimation(0f, 1f).apply { duration = 1000 }
val disapperAnim = AlphaAnimation(1f, 0f).apply { duration = 1000 }

fun ViewStub.attach(@LayoutRes layoutId: Int): View {
    layoutResource = layoutId
    return inflate()
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.alphaVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
    startAnimation(if (visible) apperAnim else disapperAnim)
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
    create: (List<T>?) -> BaseQuickAdapter<T, BaseViewHolder>
) {
    val adapter = this.adapter
    if (null == adapter) this.adapter =
        create(list) else (adapter as BaseQuickAdapter<T, BaseViewHolder>).setList(list)
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