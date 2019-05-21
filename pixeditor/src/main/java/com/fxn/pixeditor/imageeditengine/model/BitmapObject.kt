package com.fxn.pixeditor.imageeditengine.model

import android.graphics.Bitmap
import java.io.File

class BitmapObject(val path: String) {
    public var photoOrignal: File? = null
    public var orignalBitmap: Bitmap? = null
    public var mainBitmap: Bitmap? = null
}