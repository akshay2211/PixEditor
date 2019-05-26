package com.fxn.pixeditor

import com.fxn.pixeditor.imageeditengine.interfaces.AddMoreImagesListener
import java.io.Serializable

class EditOptions : Serializable {
    var selectedlist = ArrayList<String>()
    var requestCode = 0
    var addMoreImagesListener: AddMoreImagesListener? = null

    companion object {
        @JvmStatic
        fun init(): EditOptions {
            return EditOptions()
        }
    }

    private constructor()

}