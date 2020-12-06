package com.bdb.lottery.utils.ui.popup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.screen.Screens
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Inject

@ActivityScoped
open class TPopupWindow @Inject constructor(@ActivityContext private val context: Context) {
    private lateinit var mContent: View
    private lateinit var mPopWin: PopupWindow
    private var mPopWidth: Int = WindowManager.LayoutParams.WRAP_CONTENT

    fun content(convert: () -> View): TPopupWindow {
        mContent = convert()
        mPopWin = PopupWindow(mContent,
            mPopWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true)
        mPopWin.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPopWin.isOutsideTouchable = true
        mPopWin.isTouchable = true
        mPopWin.animationStyle = R.style.popwin_anim_style
        return this
    }

    fun dismiss() {
        mPopWin.dismiss()
    }

    fun getContentView(): View {
        return mContent
    }

    fun setPopWinWidth(width: Int, isDp: Boolean): TPopupWindow {
        mPopWidth = if (isDp) Sizes.dp2px(width.toFloat()) else width
        return this
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
        gravity: Int,
        x: Int,
        y: Int,
        @ALIGN align: Int = ALIGN_RIGHT,
        callback: ShowUpCallback? = null,
    ) {
        val windowPos = IntArray(2)
        val up = needShowUp(parent, mContent, windowPos, align)
        callback?.showUp(up)
        mPopWin.showAtLocation(parent, gravity, windowPos[0] + x, windowPos[1] + y)
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return 底部位置是否足够，不够pop显示再上面
     */
    private fun needShowUp(
        anchorView: View,
        contentView: View,
        windowPos: IntArray,
        @ALIGN align: Int = ALIGN_RIGHT,
    ): Boolean {
        var windowPos: IntArray? = windowPos
        if (null == windowPos) windowPos = IntArray(2)
        val anchorLoc = IntArray(2)
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc)
        val anchorHeight = anchorView.height
        // 获取屏幕的高宽
        val screenHeight: Int = Screens.screenSize()[1]
        val screenWidth: Int = Screens.screenSize()[0]
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        // 计算contentView的高宽
        val windowHeight = contentView.measuredHeight
        val windowWidth = contentView.measuredWidth
        // 判断需要向上弹出还是向下弹出显示
        val isNeedShowUp = screenHeight - anchorLoc[1] - anchorHeight < windowHeight
        val width = anchorView.width
        val width1 = mPopWin.width
        windowPos[0] =
            if (align == ALIGN_RIGHT) screenWidth - windowWidth else if (align == ALIGN_LEFT) 0 else
                anchorLoc[0] + (width - width1) / 2
        if (isNeedShowUp) {
            windowPos[1] = anchorLoc[1] - windowHeight
        } else {
            windowPos[1] = anchorLoc[1] + anchorHeight
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
@Retention(RetentionPolicy.SOURCE)
annotation class ALIGN