package com.fxn.pixeditor.imageeditengine.model

import android.graphics.Bitmap
import java.io.File

class BitmapObject(val path: String) {
    var photoOrignal: File? = null
    var orignalBitmap: Bitmap? = null
    var mainBitmap: Bitmap? = null
    var filterSelection: Int = 0
    var imageFilter: ImageFilter? = null
}