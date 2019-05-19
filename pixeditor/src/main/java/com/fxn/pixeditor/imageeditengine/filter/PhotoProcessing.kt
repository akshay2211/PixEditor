package com.fxn.pixeditor.imageeditengine.filter

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import com.fxn.pixeditor.imageeditengine.Constants
import com.fxn.pixeditor.imageeditengine.model.ImageFilter

/**
 * 图片处理类
 *
 * @author 潘易
 */
object PhotoProcessing {
    private val TAG = "PhotoProcessing"

    fun filterPhoto(bitmap: Bitmap?, filterConfig: ImageFilter): Bitmap {
        if (bitmap != null) {
            sendBitmapToNative(bitmap)
        }
        when (filterConfig.filterName) {
            Constants.FILTER_ORIGINAL // Original
            -> {
            }
            Constants.FILTER_INSTAFIX // Instafix
            -> nativeApplyInstafix()
            Constants.FILTER_ANSEL // Ansel
            -> nativeApplyAnsel()
            Constants.FILTER_TESTINO // Testino
            -> nativeApplyTestino()
            Constants.FILTER_XPRO // XPro
            -> nativeApplyXPro()
            Constants.FILTER_RETRO // Retro
            -> nativeApplyRetro()
            Constants.FILTER_BW // Black & White
            -> nativeApplyBW()
            Constants.FILTER_SEPIA // Sepia
            -> nativeApplySepia()
            Constants.FILTER_CYANO // Cyano
            -> nativeApplyCyano()
            Constants.FILTER_GEORGIA // Georgia
            -> nativeApplyGeorgia()
            Constants.FILTER_SAHARA // Sahara
            -> nativeApplySahara()
            Constants.FILTER_HDR // HDR
            -> nativeApplyHDR()
        }
        val filteredBitmap = getBitmapFromNative(bitmap)
        nativeDeleteBitmap()

        return filteredBitmap
    }

    fun combineImages(bmp1: Bitmap, bmp2: Bitmap, alpha: Int): Bitmap {
        val bmOverlay = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
        val canvas = Canvas(bmOverlay)
        Log.i("hasAlpha", bmp2.hasAlpha().toString() + "")
        canvas.drawBitmap(bmp1, Matrix(), null)
        val alphaPaint = Paint()
        alphaPaint.alpha = alpha
        canvas.drawBitmap(bmp2, 0f, 0f, alphaPaint)
        return bmOverlay
    }

    // /////////////////////////////////////////////
    init {
        System.loadLibrary("photoprocessing")
    }

    external fun nativeInitBitmap(width: Int, height: Int): Int

    external fun nativeGetBitmapRow(y: Int, pixels: IntArray)

    external fun nativeSetBitmapRow(y: Int, pixels: IntArray)

    external fun nativeGetBitmapWidth(): Int

    external fun nativeGetBitmapHeight(): Int

    external fun nativeDeleteBitmap()

    external fun nativeRotate90(): Int

    external fun nativeRotate180()

    external fun nativeFlipHorizontally()

    external fun nativeApplyInstafix()

    external fun nativeApplyCustomFilter(alpha: Int)

    external fun nativeApplyAnsel()

    external fun nativeApplyTestino()

    external fun nativeApplyXPro()

    external fun nativeApplyRetro()

    external fun nativeApplyBW()

    external fun nativeApplySepia()

    external fun nativeApplyCyano()

    external fun nativeApplyGeorgia()

    external fun nativeApplySahara()

    external fun nativeApplyHDR()

    external fun nativeLoadResizedJpegBitmap(
        jpegData: ByteArray,
        size: Int, maxPixels: Int
    )

    external fun nativeResizeBitmap(newWidth: Int, newHeight: Int)

    external fun handleSmooth(bitmap: Bitmap, smoothValue: Float)

    external fun handleWhiteSkin(bitmap: Bitmap, whiteValue: Float)

    external fun handleSmoothAndWhiteSkin(bitmap: Bitmap, smoothValue: Float, whiteValue: Float)

    external fun freeBeautifyMatrix()

    private fun sendBitmapToNative(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height
        nativeInitBitmap(width, height)
        val pixels = IntArray(width)
        for (y in 0 until height) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, 1)
            nativeSetBitmapRow(y, pixels)
        }
    }

    private fun getBitmapFromNative(bitmap: Bitmap?): Bitmap {
        var bitmap = bitmap
        val width = nativeGetBitmapWidth()
        val height = nativeGetBitmapHeight()

        if (bitmap == null || width != bitmap.width
            || height != bitmap.height || !bitmap.isMutable
        ) { // in
            var config = Config.ARGB_8888
            if (bitmap != null) {
                config = bitmap.config
                bitmap.recycle()
            }
            bitmap = Bitmap.createBitmap(width, height, config)
        }

        val pixels = IntArray(width)
        for (y in 0 until height) {
            nativeGetBitmapRow(y, pixels)
            bitmap!!.setPixels(pixels, 0, width, 0, y, width, 1)
        }

        return bitmap!!
    }

    fun makeBitmapMutable(bitmap: Bitmap): Bitmap {
        sendBitmapToNative(bitmap)
        return getBitmapFromNative(bitmap)
    }

    fun rotate(bitmap: Bitmap, angle: Int): Bitmap {
        var bitmap = bitmap
        val width = bitmap.width
        val height = bitmap.height
        val config = bitmap.config
        nativeInitBitmap(width, height)
        sendBitmapToNative(bitmap)

        if (angle == 90) {
            nativeRotate90()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(height, width, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        } else if (angle == 180) {
            nativeRotate180()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(width, height, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        } else if (angle == 270) {
            nativeRotate180()
            nativeRotate90()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(height, width, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        }
        return bitmap
    }

    fun flipHorizontally(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        nativeInitBitmap(bitmap.width, bitmap.height)
        sendBitmapToNative(bitmap)
        nativeFlipHorizontally()
        bitmap = getBitmapFromNative(bitmap)
        nativeDeleteBitmap()
        return bitmap
    }
}// end class
