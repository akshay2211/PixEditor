package com.fxn.pixeditor.imageeditengine.utils

import android.graphics.Rect
import android.graphics.RectF

/**
 * Created by panyi on 2016/6/16.
 */
object RectUtil {
    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    fun scaleRect(rect: RectF, scale: Float) {
        val w = rect.width()
        val h = rect.height()

        val newW = scale * w
        val newH = scale * h

        val dx = (newW - w) / 2
        val dy = (newH - h) / 2

        rect.left -= dx
        rect.top -= dy
        rect.right += dx
        rect.bottom += dy
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    fun rotateRect(
        rect: RectF, center_x: Float, center_y: Float,
        roatetAngle: Float
    ) {
        val x = rect.centerX()
        val y = rect.centerY()
        val sinA = Math.sin(Math.toRadians(roatetAngle.toDouble())).toFloat()
        val cosA = Math.cos(Math.toRadians(roatetAngle.toDouble())).toFloat()
        val newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA
        val newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA

        val dx = newX - x
        val dy = newY - y

        rect.offset(dx, dy)
    }

    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    fun rectAddV(srcRect: RectF?, addRect: RectF?, padding: Int) {
        if (srcRect == null || addRect == null)
            return

        val left = srcRect.left
        val top = srcRect.top
        var right = srcRect.right
        var bottom = srcRect.bottom

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width()
        }

        bottom += padding + addRect.height()

        srcRect.set(left, top, right, bottom)
    }

    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    fun rectAddV(srcRect: Rect?, addRect: Rect?, padding: Int, charMinHeight: Int) {
        if (srcRect == null || addRect == null)
            return

        val left = srcRect.left
        val top = srcRect.top
        var right = srcRect.right
        var bottom = srcRect.bottom

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width()
        }

        bottom += padding + Math.max(addRect.height(), charMinHeight)

        srcRect.set(left, top, right, bottom)
    }
}//end class
