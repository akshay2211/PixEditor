package com.fxn.pixeditor.imageeditengine.views

import android.view.View

interface ViewTouchListener {
    fun onStartViewChangeListener(view: View)
    fun onStopViewChangeListener(view: View)
    fun onStartViewFullChangeListener(view: View)
    fun onStopViewFullChangeListener(view: View)
}
