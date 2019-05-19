package com.fxn.pixeditor.imageeditengine.utils

import android.app.Activity
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

class SimpleGestureFilter(private val context: Activity, private val listener: SimpleGestureListener) :
    SimpleOnGestureListener() {
    var swipeMinDistance = 100
    var swipeMaxDistance = 350
    var swipeMinVelocity = 100

    var mode = MODE_DYNAMIC
    private var running = true
    private var tapIndicator = false
    private val detector: GestureDetector

    init {
        this.detector = GestureDetector(context, this)
    }

    fun onTouchEvent(event: MotionEvent) {

        if (!this.running) {
            return
        }

        val result = this.detector.onTouchEvent(event)

        if (this.mode == MODE_SOLID) {
            event.action = MotionEvent.ACTION_CANCEL
        } else if (this.mode == MODE_DYNAMIC) {

            if (event.action == ACTION_FAKE) {
                event.action = MotionEvent.ACTION_UP
            } else if (result) {
                event.action = MotionEvent.ACTION_CANCEL
            } else if (this.tapIndicator) {
                event.action = MotionEvent.ACTION_DOWN
                this.tapIndicator = false
            }
        }
        //else just do nothing, it's Transparent
    }

    fun setEnabled(status: Boolean) {
        this.running = status
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        var velocityX = velocityX
        var velocityY = velocityY

        val xDistance = Math.abs(e1.x - e2.x)
        val yDistance = Math.abs(e1.y - e2.y)

        if (xDistance > this.swipeMaxDistance || yDistance > this.swipeMaxDistance) {
            return false
        }

        velocityX = Math.abs(velocityX)
        velocityY = Math.abs(velocityY)
        var result = false

        if (velocityX > this.swipeMinVelocity && xDistance > this.swipeMinDistance) {
            if (e1.x > e2.x)
            // right to left
            {
                this.listener.onSwipe(SWIPE_LEFT)
            } else {
                this.listener.onSwipe(SWIPE_RIGHT)
            }

            result = true
        } else if (velocityY > this.swipeMinVelocity && yDistance > this.swipeMinDistance) {
            if (e1.y > e2.y)
            // bottom to up
            {
                this.listener.onSwipe(SWIPE_UP)
            } else {
                this.listener.onSwipe(SWIPE_DOWN)
            }

            result = true
        }

        return result
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        this.tapIndicator = true
        return false
    }

    override fun onDoubleTap(arg: MotionEvent): Boolean {
        this.listener.onDoubleTap()
        return true
    }

    override fun onDoubleTapEvent(arg: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(arg: MotionEvent): Boolean {

        if (this.mode == MODE_DYNAMIC) {        // we owe an ACTION_UP, so we fake an
            arg.action = ACTION_FAKE      //action which will be converted to an ACTION_UP later.
            this.context.dispatchTouchEvent(arg)
        }

        return false
    }

    interface SimpleGestureListener {
        fun onSwipe(direction: Int)

        fun onDoubleTap()
    }

    companion object {

        val SWIPE_UP = 1
        val SWIPE_DOWN = 2
        val SWIPE_LEFT = 3
        val SWIPE_RIGHT = 4

        val MODE_TRANSPARENT = 0
        val MODE_SOLID = 1
        val MODE_DYNAMIC = 2

        private val ACTION_FAKE = -13 //just an unlikely number
    }
}