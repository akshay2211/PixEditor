package com.fxn.pixeditor.imageeditengine.model

import android.graphics.Bitmap
import java.io.Serializable

class ImageFilter : Serializable {
    var filterName: String
    var filterImage: Bitmap? = null
    var opacity = 255
    var isSelected: Boolean = false

    constructor(filterName: String, bitmap: Bitmap) {
        this.filterName = filterName
        filterImage = bitmap
    }

    constructor(filterName: String) {
        this.filterName = filterName
    }
}
