package com.fxn.pixeditor

import java.io.Serializable

class EditOptions : Serializable {
    var selectedlist = ArrayList<String>()
    var requestCode = 0;

    companion object {
        fun init(): EditOptions {
            return EditOptions()
        }
    }

    private constructor()

}