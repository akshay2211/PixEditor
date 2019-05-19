package com.fxn.pixeditor.imageeditengine.utils

/**
 * Created by panyi on 17/3/30.
 */

object ListUtil {
    fun isEmpty(list: List<*>?): Boolean {
        return if (list == null) true else list.size == 0

    }

}//end class
