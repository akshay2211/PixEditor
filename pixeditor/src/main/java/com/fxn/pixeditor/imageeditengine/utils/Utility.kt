package com.fxn.pixeditor.imageeditengine.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.File
import java.io.FileOutputStream

object Utility {
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
    //public static Drawable tintDrawable(Context context, @DrawableRes int drawableRes, @ColorRes int colorRes){
    //  Drawable drawable = ContextCompat.getDrawable(context,drawableRes);
    //  if(drawable!=null) {
    //    drawable.mutate();
    //    DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorRes));
    //  }
    //  return drawable;
    //}

    fun tintDrawable(context: Context, @DrawableRes drawableRes: Int, colorCode: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        if (drawable != null) {
            drawable.mutate()
            DrawableCompat.setTint(drawable, colorCode)
        }
        return drawable
    }


    /**
     * Hides the soft keyboard
     */
    fun hideSoftKeyboard(context: Activity) {
        if (context.currentFocus != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(context.currentFocus!!.windowToken, 0)
        }
    }

    /**
     * Shows the soft keyboard
     */
    fun showSoftKeyboard(context: Activity, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources
            .displayMetrics
            .density
        return Math.round(dp.toFloat() * density)
    }

    fun saveBitmap(bitmap: Bitmap, imagePath: String): String? {
        return try {
            val outputFile = File(imagePath)
            //save the resized and compressed file to disk cache
            val bmpFile = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile)
            bmpFile.flush()
            bmpFile.close()
            outputFile.absolutePath
        } catch (e: Exception) {
            Log.e("show exception", "-> " + e.localizedMessage)
            null
        }

    }

    fun getCacheFilePath(context: Context): String {
        return File(
            Environment.getExternalStorageDirectory(),
            "edited_" + System.currentTimeMillis() + ".jpg"
        ).absolutePath
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeBitmap(
        imagePath: String,
        reqWidth: Int, reqHeight: Int
    ): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(imagePath, options)
    }

    fun hideTopBar(pixEditor: AppCompatActivity) {
        synchronized(pixEditor) {
            val window = pixEditor.window
            /* val decorView = window.decorView
             // Hide Status Bar.
             val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
             decorView.systemUiVisibility = uiOptions
         // clear FLAG_TRANSLUCENT_STATUS flag:
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
         // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)*/
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }
        try {
            pixEditor.supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        }

    }

    fun hideStatusBar(appCompatActivity: AppCompatActivity) {
        synchronized(appCompatActivity) {
            val w = appCompatActivity.window
            val decorView = w.decorView
            // Hide Status Bar.
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }

}
