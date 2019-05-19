package com.fxn.pixeditor.imageeditengine.views.imagezoom.graphic

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable

import java.io.InputStream

/**
 * Fast bitmap drawable. Does not support states. it only
 * support alpha and colormatrix
 * @author alessandro
 */
class FastBitmapDrawable(b: Bitmap) : Drawable(), IBitmapDrawable {

    override lateinit var bitmap: Bitmap
    protected var mPaint: Paint

    init {
        bitmap = b
        mPaint = Paint()
        mPaint.isDither = true
        mPaint.isFilterBitmap = true
    }

    constructor(res: Resources, `is`: InputStream) : this(BitmapFactory.decodeStream(`is`)) {}

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }

    override fun getMinimumWidth(): Int {
        return bitmap.width
    }

    override fun getMinimumHeight(): Int {
        return bitmap.height
    }

    fun setAntiAlias(value: Boolean) {
        mPaint.isAntiAlias = value
        invalidateSelf()
    }
}