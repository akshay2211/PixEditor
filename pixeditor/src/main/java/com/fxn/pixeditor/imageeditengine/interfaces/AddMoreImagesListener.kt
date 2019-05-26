package com.fxn.pixeditor.imageeditengine.interfaces

import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

open interface AddMoreImagesListener : Serializable {
    fun addMore(context: AppCompatActivity, list: ArrayList<String>, requestCodePix: Int)
}