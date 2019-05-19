package com.fxn.pixeditor.imageeditengine.views.imagezoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.fxn.pixeditor.imageeditengine.views.imagezoom.easing.Cubic
import com.fxn.pixeditor.imageeditengine.views.imagezoom.easing.Easing
import com.fxn.pixeditor.imageeditengine.views.imagezoom.graphic.FastBitmapDrawable
import com.fxn.pixeditor.imageeditengine.views.imagezoom.utils.IDisposable

/**
 * Base View to manage image zoom/scrool/pinch operations
 *
 * @author alessandro
 */
abstract class ImageViewTouchBase : AppCompatImageView, IDisposable {

    protected var mEasing: Easing = Cubic()
    protected var mBaseMatrix = Matrix()
    protected var mSuppMatrix = Matrix()
    protected var mNextMatrix: Matrix? = null
    protected var mHandler = Handler()
    protected var mLayoutRunnable: Runnable? = null
    protected var mUserScaled = false

    private var mMaxZoom = ZOOM_INVALID
    private var mMinZoom = ZOOM_INVALID

    // true when min and max zoom are explicitly defined
    private var mMaxZoomDefined: Boolean = false
    private var mMinZoomDefined: Boolean = false

    protected val mDisplayMatrix = Matrix()
    protected val mMatrixValues = FloatArray(9)

    private var mThisWidth = -1
    private var mThisHeight = -1
    protected val center = PointF()

    protected var mScaleType = DisplayType.NONE
    private var mScaleTypeChanged: Boolean = false
    private var mBitmapChanged: Boolean = false

    protected val DEFAULT_ANIMATION_DURATION = 200

    protected var mBitmapRect = RectF()
    protected var mCenterRect = RectF()
    protected var mScrollRect = RectF()

    private var mDrawableChangeListener: OnDrawableChangeListener? = null
    private var mOnLayoutChangeListener: OnLayoutChangeListener? = null

    /**
     * Change the display type
     */
    var displayType: DisplayType
        get() = mScaleType
        set(type) {
            if (type != mScaleType) {
                if (LOG_ENABLED) {
                    Log.i(LOG_TAG, "setDisplayType: $type")
                }
                mUserScaled = false
                mScaleType = type
                mScaleTypeChanged = true
                requestLayout()
            }
        }

    /**
     * Returns the current maximum allowed image scale
     *
     * @return
     */
    var maxScale: Float
        get() {
            if (mMaxZoom == ZOOM_INVALID) {
                mMaxZoom = computeMaxZoom()
            }
            return mMaxZoom
        }
        protected set(value) {
            if (LOG_ENABLED) {
                Log.d(LOG_TAG, "setMaxZoom: $value")
            }
            mMaxZoom = value
        }

    /**
     * Returns the current minimum allowed image scale
     *
     * @return
     */
    var minScale: Float
        get() {
            if (mMinZoom == ZOOM_INVALID) {
                mMinZoom = computeMinZoom()
            }
            return mMinZoom
        }
        protected set(value) {
            if (LOG_ENABLED) {
                Log.d(LOG_TAG, "setMinZoom: $value")
            }

            mMinZoom = value
        }

    /**
     * Returns the current view matrix
     *
     * @return
     */
    val imageViewMatrix: Matrix
        get() = getImageViewMatrix(mSuppMatrix)

    /**
     * Returns the current image display matrix.<br></br>
     * This matrix can be used in the next call to the
     * [.setImageDrawable] to restore the
     * same view state of the previous [Bitmap].<br></br>
     * Example:
     *
     * <pre>
     * Matrix currentMatrix = mImageView.getDisplayMatrix();
     * mImageView.setImageBitmap(newBitmap, currentMatrix, ZOOM_INVALID, ZOOM_INVALID);
    </pre> *
     *
     * @return the current support matrix
     */
    val displayMatrix: Matrix
        get() = Matrix(mSuppMatrix)

    val bitmapRect: RectF?
        get() = getBitmapRect(mSuppMatrix)


    fun getRotaion(): Float {
        return 0f
    }

    /**
     * Returns the current image scale
     *
     * @return
     */
    val scale: Float
        get() = getScale(mSuppMatrix)

    interface OnDrawableChangeListener {

        /**
         * Callback invoked when a new drawable has been assigned to the view
         *
         * @param drawable
         */
        fun onDrawableChanged(drawable: Drawable?)
    }

    interface OnLayoutChangeListener {
        /**
         * Callback invoked when the layout bounds changed
         *
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        fun onLayoutChanged(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    }

    /**
     * Use this to change the
     * [ImageViewTouchBase.setDisplayType] of this View
     *
     * @author alessandro
     */
    enum class DisplayType {
        /** Image is not scaled by default  */
        NONE,
        /** Image will be always presented using this view's bounds  */
        FIT_TO_SCREEN,
        /** Image will be scaled only if bigger than the bounds of this view  */
        FIT_IF_BIGGER
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    fun setOnDrawableChangedListener(listener: OnDrawableChangeListener) {
        mDrawableChangeListener = listener
    }

    fun setOnLayoutChangeListener(listener: OnLayoutChangeListener) {
        mOnLayoutChangeListener = listener
    }

    protected open fun init() {
        setScaleType(ImageView.ScaleType.MATRIX)
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType == ImageView.ScaleType.MATRIX) {
            super.setScaleType(scaleType)
        } else {
            Log.w(LOG_TAG, "Unsupported scaletype. Only MATRIX can be used")
        }
    }

    /**
     * Clear the current drawable
     */
    fun clear() {
        setImageBitmap(null)
    }

    override protected fun onLayout(
        changed: Boolean, left: Int, top: Int, right: Int,
        bottom: Int
    ) {

        if (LOG_ENABLED) {
            Log.e(
                LOG_TAG, "onLayout: " + changed + ", bitmapChanged: "
                        + mBitmapChanged + ", scaleChanged: " + mScaleTypeChanged
            )
        }

        super.onLayout(changed, left, top, right, bottom)

        var deltaX = 0
        var deltaY = 0

        if (changed) {
            val oldw = mThisWidth
            val oldh = mThisHeight

            mThisWidth = right - left
            mThisHeight = bottom - top

            deltaX = mThisWidth - oldw
            deltaY = mThisHeight - oldh

            // update center point
            center.x = mThisWidth / 2f
            center.y = mThisHeight / 2f
        }

        val r = mLayoutRunnable

        if (r != null) {
            mLayoutRunnable = null
            r.run()
        }

        val drawable = getDrawable()

        if (drawable != null) {

            if (changed || mScaleTypeChanged || mBitmapChanged) {

                var scale = 1f

                // retrieve the old values
                val old_default_scale = getDefaultScale(mScaleType)
                val old_matrix_scale = getScale(mBaseMatrix)
                val old_scale = scale
                val old_min_scale = Math.min(1f, 1f / old_matrix_scale)

                getProperBaseMatrix(drawable, mBaseMatrix)

                val new_matrix_scale = getScale(mBaseMatrix)

                if (LOG_ENABLED) {
                    Log.d(LOG_TAG, "old matrix scale: $old_matrix_scale")
                    Log.d(LOG_TAG, "new matrix scale: $new_matrix_scale")
                    Log.d(LOG_TAG, "old min scale: $old_min_scale")
                    Log.d(LOG_TAG, "old scale: $old_scale")
                }

                // 1. bitmap changed or scaletype changed
                if (mBitmapChanged || mScaleTypeChanged) {

                    if (LOG_ENABLED) {
                        Log.d(LOG_TAG, "display type: $mScaleType")
                    }

                    if (mNextMatrix != null) {
                        mSuppMatrix.set(mNextMatrix)
                        mNextMatrix = null
                        scale = scale
                    } else {
                        mSuppMatrix.reset()
                        scale = getDefaultScale(mScaleType)
                    }

                    setImageMatrix(imageViewMatrix)

                    if (scale != scale) {
                        zoomTo(scale)
                    }

                } else if (changed) {

                    // 2. layout size changed

                    if (!mMinZoomDefined)
                        mMinZoom = ZOOM_INVALID
                    if (!mMaxZoomDefined)
                        mMaxZoom = ZOOM_INVALID

                    setImageMatrix(imageViewMatrix)
                    postTranslate((-deltaX).toFloat(), (-deltaY).toFloat())

                    if (!mUserScaled) {
                        scale = getDefaultScale(mScaleType)
                        zoomTo(scale)
                    } else {
                        if (Math.abs(old_scale - old_min_scale) > 0.001) {
                            scale = old_matrix_scale / new_matrix_scale * old_scale
                        }
                        zoomTo(scale)
                    }

                    if (LOG_ENABLED) {
                        Log.d(LOG_TAG, "old min scale: $old_default_scale")
                        Log.d(LOG_TAG, "old scale: $old_scale")
                        Log.d(LOG_TAG, "new scale: $scale")
                    }

                }

                mUserScaled = false

                if (scale > maxScale || scale < minScale) {
                    // if current scale if outside the min/max bounds
                    // then restore the correct scale
                    zoomTo(scale)
                }

                center(true, true)

                if (mBitmapChanged)
                    onDrawableChanged(drawable)
                if (changed || mBitmapChanged || mScaleTypeChanged)
                    onLayoutChanged(left, top, right, bottom)

                if (mScaleTypeChanged)
                    mScaleTypeChanged = false
                if (mBitmapChanged)
                    mBitmapChanged = false

                if (LOG_ENABLED) {
                    Log.d(LOG_TAG, "new scale: $scale")
                }
            }
        } else {
            // drawable is null
            if (mBitmapChanged)
                onDrawableChanged(drawable)
            if (changed || mBitmapChanged || mScaleTypeChanged)
                onLayoutChanged(left, top, right, bottom)

            if (mBitmapChanged)
                mBitmapChanged = false
            if (mScaleTypeChanged)
                mScaleTypeChanged = false

        }
    }

    /**
     * Restore the original display
     *
     */
    fun resetDisplay() {
        mBitmapChanged = true
        requestLayout()
    }

    protected fun getDefaultScale(type: DisplayType): Float {
        return if (type == DisplayType.FIT_TO_SCREEN) {
            // always fit to screen
            1f
        } else if (type == DisplayType.FIT_IF_BIGGER) {
            // normal scale if smaller, fit to screen otherwise
            Math.min(1f, 1f / getScale(mBaseMatrix))
        } else {
            // no scale
            1f / getScale(mBaseMatrix)
        }
    }

    override fun setImageResource(resId: Int) {
        setImageDrawable(getContext().getResources().getDrawable(resId))
    }

    /**
     * {@inheritDoc} Set the new image to display and reset the internal matrix.
     *
     * @param bitmap
     * the [Bitmap] to display
     * @see {@link ImageView.setImageBitmap
     */
    override fun setImageBitmap(bitmap: Bitmap?) {
        setImageBitmap(bitmap, null, ZOOM_INVALID, ZOOM_INVALID)
    }

    /**
     * @see .setImageDrawable
     * @param bitmap
     * @param matrix
     * @param min_zoom
     * @param max_zoom
     */
    fun setImageBitmap(
        bitmap: Bitmap?, matrix: Matrix?,
        min_zoom: Float, max_zoom: Float
    ) {
        if (bitmap != null)
            setImageDrawable(
                FastBitmapDrawable(bitmap), matrix, min_zoom,
                max_zoom
            )
        else
            setImageDrawable(null, matrix, min_zoom, max_zoom)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        setImageDrawable(drawable, null, ZOOM_INVALID, ZOOM_INVALID)
    }

    /**
     *
     * Note: if the scaleType is FitToScreen then min_zoom must be <= 1 and
     * max_zoom must be >= 1
     *
     * @param drawable
     * the new drawable
     * @param initial_matrix
     * the optional initial display matrix
     * @param min_zoom
     * the optional minimum scale, pass [.ZOOM_INVALID] to use
     * the default min_zoom
     * @param max_zoom
     * the optional maximum scale, pass [.ZOOM_INVALID] to use
     * the default max_zoom
     */
    fun setImageDrawable(
        drawable: Drawable?,
        initial_matrix: Matrix?, min_zoom: Float,
        max_zoom: Float
    ) {

        val viewWidth = getWidth()

        if (viewWidth <= 0) {
            mLayoutRunnable = Runnable {
                setImageDrawable(
                    drawable, initial_matrix, min_zoom,
                    max_zoom
                )
            }
            return
        }
        _setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom)
    }

    protected open fun _setImageDrawable(
        drawable: Drawable?,
        initial_matrix: Matrix?, min_zoom: Float, max_zoom: Float
    ) {
        var min_zoom = min_zoom
        var max_zoom = max_zoom

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "_setImageDrawable")
        }

        if (drawable != null) {

            if (LOG_ENABLED) {
                Log.d(
                    LOG_TAG, "size: " + drawable.intrinsicWidth + "x"
                            + drawable.intrinsicHeight
                )
            }
            super.setImageDrawable(drawable)
        } else {
            mBaseMatrix.reset()
            super.setImageDrawable(null)
        }

        if (min_zoom != ZOOM_INVALID && max_zoom != ZOOM_INVALID) {
            min_zoom = Math.min(min_zoom, max_zoom)
            max_zoom = Math.max(min_zoom, max_zoom)

            mMinZoom = min_zoom
            mMaxZoom = max_zoom

            mMinZoomDefined = true
            mMaxZoomDefined = true

            if (mScaleType == DisplayType.FIT_TO_SCREEN || mScaleType == DisplayType.FIT_IF_BIGGER) {

                if (mMinZoom >= 1) {
                    mMinZoomDefined = false
                    mMinZoom = ZOOM_INVALID
                }

                if (mMaxZoom <= 1) {
                    mMaxZoomDefined = true
                    mMaxZoom = ZOOM_INVALID
                }
            }
        } else {
            mMinZoom = ZOOM_INVALID
            mMaxZoom = ZOOM_INVALID

            mMinZoomDefined = false
            mMaxZoomDefined = false
        }

        if (initial_matrix != null) {
            mNextMatrix = Matrix(initial_matrix)
        }

        mBitmapChanged = true
        requestLayout()
    }

    /**
     * Fired as soon as a new Bitmap has been set
     *
     * @param drawable
     */
    protected fun onDrawableChanged(drawable: Drawable?) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "onDrawableChanged")
        }
        fireOnDrawableChangeListener(drawable)
    }

    protected fun fireOnLayoutChangeListener(
        left: Int, top: Int, right: Int,
        bottom: Int
    ) {
        if (null != mOnLayoutChangeListener) {
            mOnLayoutChangeListener!!.onLayoutChanged(
                true, left, top, right,
                bottom
            )
        }
    }

    protected fun fireOnDrawableChangeListener(drawable: Drawable?) {
        if (null != mDrawableChangeListener) {
            mDrawableChangeListener!!.onDrawableChanged(drawable)
        }
    }

    /**
     * Called just after [.onLayout] if the
     * view's bounds has changed or a new Drawable has been set or the
     * [DisplayType] has been modified
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected fun onLayoutChanged(left: Int, top: Int, right: Int, bottom: Int) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "onLayoutChanged")
        }
        fireOnLayoutChangeListener(left, top, right, bottom)
    }

    protected fun computeMaxZoom(): Float {
        val drawable = getDrawable() ?: return 1f

        val fw = drawable!!.getIntrinsicWidth().toFloat() / mThisWidth.toFloat()
        val fh = drawable!!.getIntrinsicHeight().toFloat() / mThisHeight.toFloat()
        val scale = Math.max(fw, fh) * 8

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "computeMaxZoom: $scale")
        }
        return scale
    }

    protected fun computeMinZoom(): Float {
        val drawable = getDrawable() ?: return 1f

        var scale = getScale(mBaseMatrix)
        scale = Math.min(1f, 1f / scale)

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "computeMinZoom: $scale")
        }

        return scale
    }

    fun getImageViewMatrix(supportMatrix: Matrix): Matrix {
        mDisplayMatrix.set(mBaseMatrix)
        mDisplayMatrix.postConcat(supportMatrix)
        return mDisplayMatrix
    }

    override fun setImageMatrix(matrix: Matrix?) {

        val current = getImageMatrix()
        var needUpdate = false

        if (matrix == null && !current.isIdentity() || matrix != null && current != matrix) {
            needUpdate = true
        }

        super.setImageMatrix(matrix)

        if (needUpdate)
            onImageMatrixChanged()
    }

    /**
     * Called just after a new Matrix has been assigned.
     *
     * @see {@link .setImageMatrix
     */
    protected fun onImageMatrixChanged() {}

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     *
     * @param drawable
     * @param matrix
     */
    protected fun getProperBaseMatrix(drawable: Drawable, matrix: Matrix) {
        val viewWidth = mThisWidth.toFloat()
        val viewHeight = mThisHeight.toFloat()

        if (LOG_ENABLED) {
            Log.d(
                LOG_TAG, "getProperBaseMatrix. view: " + viewWidth + "x"
                        + viewHeight
            )
        }

        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        val widthScale: Float
        val heightScale: Float
        matrix.reset()

        if (w > viewWidth || h > viewHeight) {
            widthScale = viewWidth / w
            heightScale = viewHeight / h
            val scale = Math.min(widthScale, heightScale)
            matrix.postScale(scale, scale)

            val tw = (viewWidth - w * scale) / 2.0f
            val th = (viewHeight - h * scale) / 2.0f
            matrix.postTranslate(tw, th)

        } else {
            widthScale = viewWidth / w
            heightScale = viewHeight / h
            val scale = Math.min(widthScale, heightScale)
            matrix.postScale(scale, scale)

            val tw = (viewWidth - w * scale) / 2.0f
            val th = (viewHeight - h * scale) / 2.0f
            matrix.postTranslate(tw, th)
        }

        if (LOG_ENABLED) {
            printMatrix(matrix)
        }
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     *
     * @param bitmap
     * @param matrix
     */
    protected fun getProperBaseMatrix2(bitmap: Drawable, matrix: Matrix) {

        val viewWidth = mThisWidth.toFloat()
        val viewHeight = mThisHeight.toFloat()

        val w = bitmap.intrinsicWidth.toFloat()
        val h = bitmap.intrinsicHeight.toFloat()

        matrix.reset()

        val widthScale = viewWidth / w
        val heightScale = viewHeight / h

        val scale = Math.min(widthScale, heightScale)

        matrix.postScale(scale, scale)
        matrix.postTranslate((viewWidth - w * scale) / 2.0f, (viewHeight - h * scale) / 2.0f)
    }

    protected fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    fun printMatrix(matrix: Matrix) {
        val scalex = getValue(matrix, Matrix.MSCALE_X)
        val scaley = getValue(matrix, Matrix.MSCALE_Y)
        val tx = getValue(matrix, Matrix.MTRANS_X)
        val ty = getValue(matrix, Matrix.MTRANS_Y)
        Log.d(
            LOG_TAG, "matrix: { x: " + tx + ", y: " + ty + ", scalex: "
                    + scalex + ", scaley: " + scaley + " }"
        )
    }

    protected fun getBitmapRect(supportMatrix: Matrix): RectF? {
        val drawable = getDrawable() ?: return null

        val m = getImageViewMatrix(supportMatrix)
        mBitmapRect.set(
            0f, 0f, drawable!!.getIntrinsicWidth().toFloat(),
            drawable!!.getIntrinsicHeight().toFloat()
        )
        m.mapRect(mBitmapRect)
        return mBitmapRect
    }

    protected fun getScale(matrix: Matrix): Float {
        return getValue(matrix, Matrix.MSCALE_X)
    }

    protected fun center(horizontal: Boolean, vertical: Boolean) {
        val drawable = getDrawable() ?: return

        val rect = getCenter(mSuppMatrix, horizontal, vertical)

        if (rect.left != 0f || rect.top != 0f) {

            if (LOG_ENABLED) {
                Log.i(LOG_TAG, "center")
            }
            postTranslate(rect.left, rect.top)
        }
    }

    protected fun getCenter(
        supportMatrix: Matrix, horizontal: Boolean,
        vertical: Boolean
    ): RectF {
        val drawable = getDrawable() ?: return RectF(0f, 0f, 0f, 0f)

        mCenterRect.set(0f, 0f, 0f, 0f)
        val rect = getBitmapRect(supportMatrix)
        val height = rect!!.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        if (vertical) {
            val viewHeight = mThisHeight
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top
            } else if (rect.top > 0) {
                deltaY = -rect.top
            } else if (rect.bottom < viewHeight) {
                deltaY = mThisHeight - rect.bottom
            }
        }
        if (horizontal) {
            val viewWidth = mThisWidth
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left
            } else if (rect.left > 0) {
                deltaX = -rect.left
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right
            }
        }
        mCenterRect.set(deltaX, deltaY, 0f, 0f)
        return mCenterRect
    }

    protected fun postTranslate(deltaX: Float, deltaY: Float) {
        if (deltaX != 0f || deltaY != 0f) {
            if (LOG_ENABLED) {
                Log.i(LOG_TAG, "postTranslate: " + deltaX + "x" + deltaY)
            }
            mSuppMatrix.postTranslate(deltaX, deltaY)
            setImageMatrix(imageViewMatrix)
        }
    }

    protected fun postScale(scale: Float, centerX: Float, centerY: Float) {
        if (LOG_ENABLED) {
            Log.i(
                LOG_TAG, "postScale: " + scale + ", center: " + centerX + "x"
                        + centerY
            )
        }
        mSuppMatrix.postScale(scale, scale, centerX, centerY)
        setImageMatrix(imageViewMatrix)
    }

    protected fun zoomTo(scale: Float) {
        var scale = scale
        if (scale > maxScale)
            scale = maxScale
        if (scale < minScale)
            scale = minScale

        val center = center
        zoomTo(scale, center.x, center.y)
    }

    /**
     * Scale to the target scale
     *
     * @param scale
     * the target zoom
     * @param durationMs
     * the animation duration
     */
    fun zoomTo(scale: Float, durationMs: Float) {
        val center = center
        zoomTo(scale, center.x, center.y, durationMs)
    }

    protected fun zoomTo(scale: Float, centerX: Float, centerY: Float) {
        var scale = scale
        if (scale > maxScale)
            scale = maxScale

        val oldScale = scale
        val deltaScale = scale / oldScale
        postScale(deltaScale, centerX, centerY)
        onZoom(scale)
        center(true, true)
    }

    protected fun onZoom(scale: Float) {}

    protected open fun onZoomAnimationCompleted(scale: Float) {}

    /**
     * Scrolls the view by the x and y amount
     *
     * @param x
     * @param y
     */
    fun scrollBy(x: Float, y: Float) {
        panBy(x.toDouble(), y.toDouble())
    }

    protected fun panBy(dx: Double, dy: Double) {
        val rect = bitmapRect
        mScrollRect.set(dx.toFloat(), dy.toFloat(), 0f, 0f)
        updateRect(rect, mScrollRect)
        postTranslate(mScrollRect.left, mScrollRect.top)
        center(true, true)
    }

    protected fun updateRect(bitmapRect: RectF?, scrollRect: RectF) {
        if (bitmapRect == null)
            return

        if (bitmapRect.top >= 0 && bitmapRect.bottom <= mThisHeight)
            scrollRect.top = 0f
        if (bitmapRect.left >= 0 && bitmapRect.right <= mThisWidth)
            scrollRect.left = 0f
        if (bitmapRect.top + scrollRect.top >= 0 && bitmapRect.bottom > mThisHeight)
            scrollRect.top = (0 - bitmapRect.top).toInt().toFloat()
        if (bitmapRect.bottom + scrollRect.top <= mThisHeight - 0 && bitmapRect.top < 0)
            scrollRect.top = (mThisHeight - 0 - bitmapRect.bottom).toInt().toFloat()
        if (bitmapRect.left + scrollRect.left >= 0)
            scrollRect.left = (0 - bitmapRect.left).toInt().toFloat()
        if (bitmapRect.right + scrollRect.left <= mThisWidth - 0)
            scrollRect.left = (mThisWidth - 0 - bitmapRect.right).toInt().toFloat()
    }

    protected fun scrollBy(
        distanceX: Float, distanceY: Float,
        durationMs: Double
    ) {
        val dx = distanceX.toDouble()
        val dy = distanceY.toDouble()
        val startTime = System.currentTimeMillis()
        mHandler.post(object : Runnable {

            internal var old_x = 0.0
            internal var old_y = 0.0

            override fun run() {
                val now = System.currentTimeMillis()
                val currentMs = Math.min(durationMs, (now - startTime).toDouble())
                val x = mEasing.easeOut(currentMs, 0.0, dx, durationMs)
                val y = mEasing.easeOut(currentMs, 0.0, dy, durationMs)
                panBy(x - old_x, y - old_y)
                old_x = x
                old_y = y
                if (currentMs < durationMs) {
                    mHandler.post(this)
                } else {
                    val centerRect = getCenter(mSuppMatrix, true, true)
                    if (centerRect.left != 0f || centerRect.top != 0f)
                        scrollBy(centerRect.left, centerRect.top)
                }
            }
        })
    }

    protected fun zoomTo(
        scale: Float, centerX: Float, centerY: Float,
        durationMs: Float
    ) {
        var scale = scale
        if (scale > maxScale)
            scale = maxScale

        val startTime = System.currentTimeMillis()
        val oldScale = scale

        val deltaScale = scale - oldScale

        val m = Matrix(mSuppMatrix)
        m.postScale(scale, scale, centerX, centerY)
        val rect = getCenter(m, true, true)

        val destX = centerX + rect.left * scale
        val destY = centerY + rect.top * scale

        mHandler.post(object : Runnable {

            override fun run() {
                val now = System.currentTimeMillis()
                val currentMs = Math.min(durationMs, (now - startTime).toFloat())
                val newScale = mEasing.easeInOut(
                    currentMs.toDouble(), 0.0,
                    deltaScale.toDouble(), durationMs.toDouble()
                ) as Float
                zoomTo(oldScale + newScale, destX, destY)
                if (currentMs < durationMs) {
                    mHandler.post(this)
                } else {
                    onZoomAnimationCompleted(scale)
                    center(true, true)
                }
            }
        })
    }

    override fun dispose() {
        clear()
    }

    companion object {

        val LOG_TAG = "ImageViewTouchBase"
        public val LOG_ENABLED = false

        val ZOOM_INVALID = -1f
    }
}
