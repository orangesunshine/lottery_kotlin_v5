package com.bdb.lottery.utils.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TImage @Inject constructor(){
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val drawingCacheEnabled = view.isDrawingCacheEnabled
        val willNotCacheDrawing = view.willNotCacheDrawing()
        view.isDrawingCacheEnabled = true
        view.setWillNotCacheDrawing(false)
        var drawingCache = view.drawingCache
        val bitmap: Bitmap
        if (null == drawingCache || drawingCache.isRecycled) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            drawingCache = view.drawingCache
            if (null == drawingCache || drawingCache.isRecycled) {
                bitmap = Bitmap.createBitmap(view.measuredWidth,
                    view.measuredHeight,
                    Bitmap.Config.RGB_565)
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
}