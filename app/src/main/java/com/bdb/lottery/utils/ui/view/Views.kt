package com.bdb.lottery.utils.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import androidx.annotation.RequiresApi
import com.bdb.lottery.app.BdbApp

object Views {
    fun layoutId2View(layoutId: Int): View {
        return layoutId2View(BdbApp.context, layoutId)
    }

    fun layoutId2View(context: Context, layoutId: Int): View {
        return View.inflate(context, layoutId, null)
    }

    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val drawingCacheEnabled = view.isDrawingCacheEnabled
        val willNotCacheDrawing = view.willNotCacheDrawing()
        view.isDrawingCacheEnabled = true
        view.setWillNotCacheDrawing(false)
        var drawingCache = view.drawingCache
        val bitmap: Bitmap
        if (null == drawingCache || drawingCache.isRecycled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            drawingCache = view.drawingCache
            if (null == drawingCache || drawingCache.isRecycled) {
                bitmap = Bitmap.createBitmap(
                    view.measuredWidth,
                    view.measuredHeight,
                    Bitmap.Config.RGB_565
                )
                val canvas = Canvas(bitmap)
                view.draw(canvas)
            } else {
                bitmap = Bitmap.createBitmap(drawingCache)
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache)
        }
        view.setWillNotCacheDrawing(willNotCacheDrawing)
        view.isDrawingCacheEnabled = drawingCacheEnabled
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun view2Bitmap(activity: Activity, view: View, callback: (Bitmap) -> Unit) {
        activity.window?.let {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val loc = IntArray(2)
            view.getLocationInWindow(loc)
            try {
                PixelCopy.request(
                    it,
                    Rect(loc[0], loc[1], loc[0] + view.width, loc[1] + view.height),
                    bitmap,
                    { result ->
                        if (result == PixelCopy.SUCCESS) {
                            callback.invoke(bitmap)
                        }
                    },
                    Handler()
                )
            } catch (e: Exception) {
            }
        }
    }
}