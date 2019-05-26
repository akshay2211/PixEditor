package com.fxn.pixeditor.imageeditengine.interfaces

import java.io.Serializable


/**
 * Created by akshay on 05/07/18.
 */
interface WorkFinish : Serializable {
    fun onWorkFinish(check: Boolean?)
}
