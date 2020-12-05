package com.bdb.lottery.dialog

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import com.bdb.lottery.extension.visible
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.confirm_dialog.*
import javax.inject.Inject

@ActivityScoped
class ConfirmDialog @Inject constructor() : BaseDialog(R.layout.confirm_dialog) {
    private var mConfirmListener: View.OnClickListener? = null
    private var mCancelListener: View.OnClickListener? = null
    private var mSingleBt = false
    private var mContentText: String? = null
    private var mContent: (() -> View)? = null

    fun onConfirm(confirm: View.OnClickListener): ConfirmDialog {
        mConfirmListener = confirm
        return this
    }

    fun onCancel(cancel: View.OnClickListener): ConfirmDialog {
        mCancelListener = cancel
        return this
    }

    fun singleBt(): ConfirmDialog {
        mSingleBt = true
        return this
    }

    fun contentText(contentText: String?): ConfirmDialog {
        mContentText = contentText
        return this
    }

    fun content(content: (() -> View)? = null): ConfirmDialog {
        mContent = content
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMargin = 48f
        confirm_confirm_btn.setOnClickListener {
            dismissAllowingStateLoss()
            mConfirmListener?.onClick(it)
        }
        confirm_cancel_bt.setOnClickListener {
            dismissAllowingStateLoss()
            mCancelListener?.onClick(it)
        }
        confirm_cancel_bt.visible(!mSingleBt)
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT).apply { gravity = CENTER }
        confirm_content_fl.addView(mContent?.invoke() ?: let {
            TextView(context).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
                setTextColor(Color.parseColor("#484848"))
                text = mContentText
            }
        }, lp)

    }
}