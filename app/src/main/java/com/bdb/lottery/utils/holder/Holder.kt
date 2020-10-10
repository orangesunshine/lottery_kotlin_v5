package com.bdb.lottery.utils.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface Holder {
    /**
     * 获取根控件
     *
     * @return
     */
    fun getRootView(): View?

    /**
     * 根据viewId获取控件
     *
     * @param viewId
     * @param <T>
     * @return
    </T> */
    fun <T : View?> getView(viewId: Int): T

    /**
     * 根据viewId获取控件
     *
     * @param viewId
     * @return
     */
    fun getTextView(viewId: Int): TextView?

    /**
     * 根据viewId获取控件
     *
     * @param viewId
     * @return
     */
    fun getImageView(viewId: Int): ImageView?

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     * @return
     */
    fun setText(viewId: Int, text: CharSequence?): Holder?

    /**
     * 设置文本
     *
     * @param tv
     * @param text
     * @return
     */
    fun setText(
        tv: TextView?,
        text: CharSequence?
    ): Holder?

    /**
     * 设置文本
     *
     * @param viewId
     * @param stringRes
     */
    fun setText(viewId: Int, @StringRes stringRes: Int): Holder?

    /**
     * 设置文本
     *
     * @param tv
     * @param stringRes
     */
    fun setText(
        tv: TextView?,
        @StringRes stringRes: Int
    ): Holder?

    /**
     * 设置文本大小
     *
     * @param viewId
     * @param size
     * @return
     */
    fun setTextSize(viewId: Int, size: Float): Holder?

    /**
     * 设置文本大小
     *
     * @param tv
     * @param size
     * @return
     */
    fun setTextSize(tv: TextView?, size: Float): Holder?

    /**
     * 设置文本颜色
     *
     * @param viewId
     * @param color
     * @return
     */
    fun setTextColor(viewId: Int, @ColorRes color: Int): Holder?

    /**
     * 设置文本颜色
     *
     * @param tv
     * @param color
     * @return
     */
    fun setTextColor(
        tv: TextView?,
        @ColorRes color: Int
    ): Holder?

    /**
     * 设置图片资源
     *
     * @param viewId
     * @param resId
     * @return
     */
    fun setImageResource(viewId: Int, resId: Int): Holder?

    /**
     * 设置图片资源
     *
     * @param iv
     * @param resId
     * @return
     */
    fun setImageResource(
        iv: ImageView?,
        resId: Int
    ): Holder?

    /**
     * 设置背景图片资源
     *
     * @param viewId
     * @param resId
     * @return
     */
    fun setBackgroundResource(viewId: Int, resId: Int): Holder?

    /**
     * 设置背景图片资源
     *
     * @param view
     * @param resId
     * @return
     */
    fun setBackgroundResource(
        view: View?,
        resId: Int
    ): Holder?

    /**
     * 加载gif图片资源
     *
     * @param viewId
     * @param resId
     * @return
     */
    fun loadImageResourceAsGif(viewId: Int, resId: Int): Holder?

    /**
     * 加载gif图片资源
     *
     * @param iv
     * @param resId
     * @return
     */
    fun loadImageResourceAsGif(
        iv: ImageView?,
        resId: Int
    ): Holder?

    /**
     * viewId设置可见性
     *
     * @param viewId
     * @param visible
     * @return
     */
    fun setVisible(viewId: Int, visible: Boolean): Holder?

    /**
     * viewId设置可见性
     *
     * @param viewId
     * @param visible
     * @param print   打印日志
     * @return
     */
    fun setVisible(
        viewId: Int,
        visible: Boolean,
        print: Boolean
    ): Holder?

    /**
     * view设置可见性
     *
     * @param view
     * @param visible
     * @return
     */
    fun setVisible(
        view: View?,
        visible: Boolean
    ): Holder?

    /**
     * viewId设置可见性
     *
     * @param view
     * @param visible
     * @param print   打印日志
     * @return
     */
    fun setVisible(
        view: View?,
        visible: Boolean,
        print: Boolean
    ): Holder?

    /**
     * viewId设置选中
     *
     * @param viewId
     * @param selected
     * @return
     */
    fun setSelect(viewId: Int, selected: Boolean): Holder?

    /**
     * view设置选中
     *
     * @param view
     * @param selected
     * @return
     */
    fun setSelect(
        view: View?,
        selected: Boolean
    ): Holder?

    /**
     * 动态设置控件高度
     *
     * @param viewId
     * @param height
     * @return
     */
    fun setHeight(viewId: Int, height: Int): Holder?

    /**
     * 动态设置控件高度
     *
     * @param view
     * @param height
     * @return
     */
    fun setHeight(view: View?, height: Int): Holder?

    /**
     * 动态设置控件宽度
     *
     * @param viewId
     * @param width
     * @return
     */
    fun setWidth(viewId: Int, width: Int): Holder?

    /**
     * 动态设置控件宽度
     *
     * @param view
     * @param width
     * @return
     */
    fun setWidth(view: View?, width: Int): Holder?

    /**
     * 设置项点击事件
     *
     * @param listener
     * @return
     */
    fun setOnItemClick(listener: OnItemClickListener?): Holder?

    /**
     * 设置项子控件点击事件
     *
     * @param listener
     * @param viewIds
     * @return
     */
    fun addOnItemChildClick(
        listener: OnItemChildClickListener?,
        vararg viewIds: Int
    ): Holder?

    /**
     * 清空view容器
     */
    fun clear()

    /**
     * 项子控件点击事件监听
     */
    interface OnItemChildClickListener {
        fun onItemChildClick(v: View?)
    }

    /**
     * 项点击事件监听
     */
    interface OnItemClickListener {
        fun onItemClick(v: View?)
    }
}