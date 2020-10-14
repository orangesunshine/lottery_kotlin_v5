/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bdb.lottery.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bdb.lottery.R
import java.util.*

class LoadingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.styleLoadingLayout
) : FrameLayout(context, attrs, defStyleAttr) {
    private val STATE_CONTENT = 1 shl 0
    private val STATE_ERROR = 1 shl 1
    private val STATE_EMPTY = 1 shl 2
    private val STATE_LOADING = 1 shl 3
    private var mState = STATE_CONTENT

    var mEmptyImage: Int
    var mEmptyText: CharSequence?
    var mErrorImage: Int
    var mErrorText: CharSequence?
    var mRetryListener: OnClickListener? = null
    var mTextColor: Int
    var mTextSize: Int

    //    int mButtonTextColor, mButtonTextSize;
    //    Drawable mButtonBackground;
    var mEmptyLayoutId = NO_ID
    var mLoadingLayoutId = NO_ID
    var mErrorLayoutId = NO_ID
    var mContentLayoutId = NO_ID
    var mLayouts: MutableMap<Int, View> = HashMap()
    var mInflater: LayoutInflater
    fun dp2px(dp: Float): Int {
        return (resources.displayMetrics.density * dp).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        id = R.id.id_common_loadinglayout
        if (childCount == 0) {
            return
        }
        if (childCount == 1) {
            val view = getChildAt(0)
            view?.let {
                if (R.id.id_common_content_layout != it.id) it.id = R.id.id_common_content_layout
                setContentView(it)
            }
        }

        if (childCount > 1) {
            for (i in 0..childCount) {
                val view = getChildAt(i)
                view?.let { if (R.id.id_common_content_layout == it.id) setContentView(it) }
                removeView(view)
            }
        }
    }

    private fun setContentView(view: View) {
        mContentLayoutId = view.id
        mLayouts[mContentLayoutId] = view
    }

    fun setEmptyImage(@DrawableRes resId: Int): LoadingLayout {
        mEmptyImage = resId
        image(mEmptyLayoutId, R.id.id_common_empty_image, mEmptyImage)
        return this
    }

    fun setEmptyText(value: String?): LoadingLayout {
        mEmptyText = value
        text(mEmptyLayoutId, R.id.id_common_empty_text, mEmptyText)
        return this
    }

    fun setErrorImage(@DrawableRes resId: Int): LoadingLayout {
        mErrorImage = resId
        image(mErrorLayoutId, R.id.id_common_error_image, mErrorImage)
        return this
    }

    fun setErrorText(value: CharSequence?): LoadingLayout {
        mErrorText = value
        text(mErrorLayoutId, R.id.id_common_error_text, mErrorText)
        return this
    }

    fun setRetryListener(listener: OnClickListener?): LoadingLayout {
        mRetryListener = listener
        return this
    }

    fun showLoading() {
        if (STATE_LOADING == mState) return
        show(mLoadingLayoutId)
        mState = STATE_LOADING
    }

    fun showEmpty() {
        if (STATE_EMPTY == mState) return
        show(mEmptyLayoutId)
        mState = STATE_EMPTY
    }

    fun showError() {
        if (STATE_ERROR == mState) return
        show(mErrorLayoutId)
        mState = STATE_ERROR
    }

    fun showContent() {
        if (STATE_CONTENT == mState) return
        show(mContentLayoutId)
        mState = STATE_CONTENT
    }

    val loading: View?
        get() = layout(mLoadingLayoutId)
    val empty: View?
        get() = layout(mEmptyLayoutId)
    val error: View?
        get() = layout(mErrorLayoutId)
    val content: View?
        get() = layout(mContentLayoutId)

    private fun show(layoutId: Int) {
        for (view in mLayouts.values) {
            view.visibility = GONE
        }
        layout(layoutId)?.visibility = VISIBLE
    }

    private fun remove(layoutId: Int) {
        if (mLayouts.containsKey(layoutId)) {
            val vg = mLayouts.remove(layoutId)
            removeView(vg)
        }
    }

    private fun layout(layoutId: Int): View? {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts[layoutId]
        }
        val layout = mInflater.inflate(layoutId, this, false)
        layout.visibility = GONE
        addView(layout)
        mLayouts[layoutId] = layout
        if (layoutId == mEmptyLayoutId) {
            val img = layout.findViewById<View>(R.id.id_common_empty_image) as ImageView
            img?.setImageResource(mEmptyImage)
            val view = layout.findViewById<View>(R.id.id_common_empty_text) as TextView
            if (view != null) {
                view.text = mEmptyText
                view.setTextColor(mTextColor)
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
            }
        } else if (layoutId == mErrorLayoutId) {
            val img = layout.findViewById<View>(R.id.id_common_error_image) as ImageView
            img?.setImageResource(mErrorImage)
            val txt = layout.findViewById<View>(R.id.id_common_error_text) as TextView
            if (txt != null) {
                txt.text = mErrorText
                txt.setTextColor(mTextColor)
                txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
            }
            mRetryListener?.let { layout.setOnClickListener(it) }
        }
        return layout
    }

    private fun text(layoutId: Int, ctrlId: Int, value: CharSequence?) {
        if (mLayouts.containsKey(layoutId)) {
            mLayouts[layoutId]?.findViewById<TextView>(ctrlId)?.text = value
        }
    }

    private fun image(layoutId: Int, ctrlId: Int, resId: Int) {
        if (mLayouts.containsKey(layoutId)) {
            val view = mLayouts[layoutId]?.findViewById<View>(ctrlId) as ImageView
            view?.setImageResource(resId)
        }
    }

    companion object {
        fun wrap(activity: Activity): LoadingLayout {
            return wrap(
                (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(
                    0
                )
            )
        }

        fun wrap(fragment: Fragment): LoadingLayout {
            return wrap(fragment.view)
        }

        fun wrap(view: View?): LoadingLayout {
            if (view == null) {
                throw RuntimeException("content view can not be null")
            }
            val parent = view.parent as ViewGroup
            if (view == null) {
                throw RuntimeException("parent view can not be null")
            }
            val lp = view.layoutParams
            val index = parent.indexOfChild(view)
            val weight = lp is LinearLayout.LayoutParams && lp.weight > 0
            val layout = LoadingLayout(view.context)
            parent.removeView(view)
            parent.addView(
                layout,
                index,
                if (weight) lp else ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            layout.addView(
                view,
                if (weight) ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ) else lp
            )
            layout.setContentView(view)
            return layout
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingLayout,
            defStyleAttr,
            R.style.LoadingLayout_Style
        )
        mEmptyImage = a.getResourceId(R.styleable.LoadingLayout_llEmptyImage, NO_ID)
        mEmptyText = a.getString(R.styleable.LoadingLayout_llEmptyText)
        mErrorImage = a.getResourceId(R.styleable.LoadingLayout_llErrorImage, NO_ID)
        mErrorText = a.getString(R.styleable.LoadingLayout_llErrorText)
        mTextColor = a.getColor(R.styleable.LoadingLayout_llTextColor, 0x333333)
        mTextSize = a.getDimensionPixelSize(R.styleable.LoadingLayout_llTextSize, dp2px(16f))
        mEmptyLayoutId =
            a.getResourceId(R.styleable.LoadingLayout_llEmptyResId, R.layout.layout_common_empty)
        mLoadingLayoutId = a.getResourceId(
            R.styleable.LoadingLayout_llLoadingResId,
            R.layout.layout_common_loading
        )
        mErrorLayoutId =
            a.getResourceId(R.styleable.LoadingLayout_llErrorResId, R.layout.layout_common_error)
        a.recycle()
    }
}