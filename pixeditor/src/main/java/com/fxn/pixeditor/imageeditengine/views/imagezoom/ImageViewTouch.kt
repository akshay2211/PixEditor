package com.fxn.pixeditor.imageeditengine.views.imagezoom

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ViewConfiguration

class ImageViewTouch(context: Context, attrs: AttributeSet) : ImageViewTouchBase(context, attrs) {

    protected lateinit var mScaleDetector: ScaleGestureDetector
    protected lateinit var mGestureDetector: GestureDetector
    protected var mTouchSlop: Int = 0
    protected var mScaleFactor: Float = 0.toFloat()
    protected var mDoubleTapDirection: Int = 0
    protected lateinit var mGestureListener: OnGestureListener
    protected lateinit var mScaleListener: OnScaleGestureListener
    var doubleTapEnabled = true
    protected var mScaleEnabled = true
    protected var mScrollEnabled = true
    private var mDoubleTapListener: OnImageViewTouchDoubleTapListener? = null
    private var mSingleTapListener: OnImageViewTouchSingleTapListener? = null
    private var mFlingListener: OnImageFlingListener? = null

    protected val gestureListener: OnGestureListener
        get() = GestureListener()

    protected val scaleListener: OnScaleGestureListener
        get() = ScaleListener()

    override fun init() {
        super.init()
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mGestureListener = gestureListener
        mScaleListener = scaleListener
        mScaleDetector = ScaleGestureDetector(context, mScaleListener)
        mGestureDetector = GestureDetector(context, mGestureListener, null, true)

        mDoubleTapDirection = 1
    }

    fun setDoubleTapListener(listener: OnImageViewTouchDoubleTapListener) {
        mDoubleTapListener = listener
    }

    fun setSingleTapListener(listener: OnImageViewTouchSingleTapListener) {
        mSingleTapListener = listener
    }

    fun setFlingListener(listener: OnImageFlingListener) {
        mFlingListener = listener
    }

    fun setScaleEnabled(value: Boolean) {
        mScaleEnabled = value
        doubleTapEnabled = value
    }

    fun setScrollEnabled(value: Boolean) {
        mScrollEnabled = value
    }

    override fun _setImageDrawable(
        drawable: Drawable?,
        initial_matrix: Matrix?, min_zoom: Float, max_zoom: Float
    ) {
        super._setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom)
        mScaleFactor = maxScale / 3
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)

        if (!mScaleDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event)
        }

        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> if (scale < minScale) {
                zoomTo(minScale, 500f)
            }
        }
        return true
    }

    override fun onZoomAnimationCompleted(scale: Float) {

        if (ImageViewTouchBase.LOG_ENABLED) {
            Log.d(
                ImageViewTouchBase.LOG_TAG, "onZoomAnimationCompleted. scale: " + scale
                        + ", minZoom: " + minScale
            )
        }

        if (scale < minScale) {
            zoomTo(minScale, 50f)
        }
    }

    // protected float onDoubleTapPost(float scale, float maxZoom) {
    // if (mDoubleTapDirection == 1) {
    // mDoubleTapDirection = -1;
    // return maxZoom;
    // } else {
    // mDoubleTapDirection = 1;
    // return 1f;
    // }
    // }

    protected fun onDoubleTapPost(scale: Float, maxZoom: Float): Float {
        if (mDoubleTapDirection == 1) {
            if (scale + mScaleFactor * 2 <= maxZoom) {
                return scale + mScaleFactor
            } else {
                mDoubleTapDirection = -1
                return maxZoom
            }
        } else {
            mDoubleTapDirection = 1
            return 1f
        }
    }

    fun onScroll(
        e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (!mScrollEnabled)
            return false

        if (e1 == null || e2 == null)
            return false
        if (e1.pointerCount > 1 || e2.pointerCount > 1)
            return false
        if (mScaleDetector.isInProgress)
            return false
        if (scale == 1f)
            return false

        mUserScaled = true
        // scrollBy(distanceX, distanceY);
        scrollBy(-distanceX, -distanceY)
        // RectF r = getBitmapRect();
        // System.out.println(r.left + "   " + r.top + "   " + r.right + "   "
        // + r.bottom);
        invalidate()
        return true
    }

    /**
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
    fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (!mScrollEnabled)
            return false

        if (mFlingListener != null) {
            mFlingListener!!.onFling(e1, e2, velocityX, velocityY)
        }

        if (e1.pointerCount > 1 || e2.pointerCount > 1)
            return false
        if (mScaleDetector.isInProgress)
            return false
        if (scale == 1f)
            return false

        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true
            // System.out.println("on fling scroll");
            scrollBy(diffX / 2, diffY / 2, 300.0)
            invalidate()
            return true
        }

        return false
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     * negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    fun canScroll(direction: Int): Boolean {
        val bitmapRect = bitmapRect
        updateRect(bitmapRect, mScrollRect)
        val imageViewRect = Rect()
        getGlobalVisibleRect(imageViewRect)

        if (null == bitmapRect) {
            return false
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD
            }
        }

        val bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left).toDouble()
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD
    }

    /**
     * @author
     */
    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

            if (null != mSingleTapListener) {
                mSingleTapListener!!.onSingleTapConfirmed()
            }

            return super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.i(ImageViewTouchBase.LOG_TAG, "onDoubleTap. double tap enabled? $doubleTapEnabled")
            if (doubleTapEnabled) {
                mUserScaled = true
                val scale = scale
                var targetScale = scale
                targetScale = onDoubleTapPost(scale, maxScale)
                targetScale = Math.min(
                    maxScale,
                    Math.max(targetScale, minScale)
                )
                zoomTo(
                    targetScale, e.x, e.y,
                    DEFAULT_ANIMATION_DURATION.toFloat()
                )
                invalidate()
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener!!.onDoubleTap()
            }

            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress) {
                    setPressed(true)
                    performLongClick()
                }
            }
        }

        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            return this@ImageViewTouch.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float
        ): Boolean {
            return this@ImageViewTouch.onFling(e1, e2, velocityX, velocityY)
        }
    }// end inner class

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        protected var mScaled = false

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val span = detector.currentSpan - detector.previousSpan
            var targetScale = scale * detector.scaleFactor
            // System.out.println("span--->" + span);
            if (mScaleEnabled) {
                if (mScaled && span != 0f) {
                    mUserScaled = true
                    targetScale = Math.min(
                        maxScale,
                        Math.max(targetScale, minScale - 0.1f)
                    )
                    zoomTo(
                        targetScale, detector.focusX,
                        detector.focusY
                    )
                    mDoubleTapDirection = 1
                    invalidate()
                    return true
                }

                // This is to prevent a glitch the first time
                // image is scaled.
                if (!mScaled)
                    mScaled = true
            }
            return true
        }
    }// end inner class

    fun resetImage() {
        val scale = scale
        var targetScale = scale
        targetScale = Math.min(
            maxScale,
            Math.max(targetScale, minScale)
        )
        zoomTo(targetScale, 0f, 0f, DEFAULT_ANIMATION_DURATION.toFloat())
        invalidate()
    }

    interface OnImageViewTouchDoubleTapListener {

        fun onDoubleTap()
    }

    interface OnImageViewTouchSingleTapListener {

        fun onSingleTapConfirmed()
    }

    interface OnImageFlingListener {
        fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float)
    }

    companion object {
        internal val SCROLL_DELTA_THRESHOLD = 1.0f
    }
}
