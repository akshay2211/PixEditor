package com.fxn.pixeditor.imageeditengine.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var scrollerEnabled = true


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (scrollerEnabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (scrollerEnabled) {
            super.onInterceptTouchEvent(event)
        } else false

    }

}
