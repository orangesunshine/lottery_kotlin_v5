package com.bdb.lottery.utils.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.screen.Screens
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.annotation.RetentionPolicy
import javax.inject.Inject

@ActivityScoped
open class TPopupWindow @Inject constructor() {
    protected lateinit var mContent: ViewGroup
    protected lateinit var mPopWin: PopupWindow

    fun content(
        width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        convert: () -> ViewGroup,
    ): TPopupWindow {
        mContent = convert()
        mContent.isFocusable = false
        mContent.isFocusableInTouchMode = false
        mPopWin = PopupWindow(
            mContent,
            width,
            height,
            true
        )
        mPopWin.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPopWin.isOutsideTouchable = true
        mPopWin.isTouchable = true
        mPopWin.animationStyle = R.style.popwin_anim_style
        return this
    }

    fun isShow(): Boolean {
        return mPopWin.isShowing
    }

    fun dismiss() {
        mPopWin.dismiss()
    }

    fun getContentView(): View {
        return mContent
    }

    open fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        mPopWin.showAtLocation(parent, gravity, x, y)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    open fun showAsDropDown(parent: View?, gravity: Int, x: Int, y: Int) {
        mPopWin.showAsDropDown(parent, x, y, gravity)
    }

    open fun showAtScreenLocation(
        parent: View,
        gravity: Int = Gravity.TOP or Gravity.START,
        xOffset: Int = 0,
        yOffset: Int = 0,
        @ALIGN align: Int = ALIGN_RIGHT,
        callback: ShowUpCallback? = null,
    ) {
        val windowPos = IntArray(2)
        val up = needShowUp(parent, mContent, windowPos, xOffset, yOffset, align)
        callback?.showUp(up)
        mPopWin.showAtLocation(parent, gravity, windowPos[0], windowPos[1])
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return 底部位置是否足够，不够pop显示再上面
     */
    fun needShowUp(
        anchorView: View,
        contentView: View,
        windowPos: IntArray?,
        xOffset: Int = 0,
        yOffset: Int = 0,
        @ALIGN align: Int = ALIGN_RIGHT,
    ): Boolean {
        val windowPosInner: IntArray = windowPos?:IntArray(2)
        val anchorLoc = IntArray(2)
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc)
        val anchorHeight = anchorView.height
        // 获取屏幕的高宽
        val screenHeight: Int = Screens.screenSize()[1]
        val screenWidth: Int = Screens.screenSize()[0]

        var height = mPopWin.height
        var width = mPopWin.width
        if (width <= 0 || height <= 0) {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            // 计算contentView的高宽
            if (height <= 0)
                height = contentView.measuredHeight
            if (width <= 0)
                width = contentView.measuredWidth
        }
        // 判断需要向上弹出还是向下弹出显示
        val isNeedShowUp = screenHeight - anchorLoc[1] - anchorHeight < height
        val anchorViewWidth = anchorView.width
        windowPosInner[0] =
            if (align == ALIGN_RIGHT) screenWidth - width - xOffset else if (align == ALIGN_LEFT) xOffset else
                anchorLoc[0] + (anchorViewWidth - width) / 2 + xOffset
        if (isNeedShowUp) {
            windowPosInner[1] = anchorLoc[1] - height - yOffset
        } else {
            windowPosInner[1] = anchorLoc[1] + anchorHeight + yOffset
        }
        return isNeedShowUp
    }

    interface ShowUpCallback {
        fun showUp(up: Boolean)
    }
}

const val ALIGN_LEFT = 0
const val ALIGN_RIGHT = 1
const val ALIGN_ANCHOR = 2

@IntDef(ALIGN_LEFT, ALIGN_RIGHT, ALIGN_ANCHOR)
@Retention(AnnotationRetention.SOURCE)
annotation class ALIGN