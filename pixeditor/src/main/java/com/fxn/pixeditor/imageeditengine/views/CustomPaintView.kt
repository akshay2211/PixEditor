package com.fxn.pixeditor.imageeditengine.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi

/**
 * Created by panyi on 17/2/11.
 */

class CustomPaintView : View {
    private var mPaint: Paint? = null
    var paintBit: Bitmap? = null
        private set
    private var mEraserPaint: Paint? = null

    private var mPaintCanvas: Canvas? = null

    private var last_x: Float = 0.toFloat()
    private var last_y: Float = 0.toFloat()
    private var eraser: Boolean = false

    private var mColor: Int = 0
    private var bounds: RectF? = null

    var color: Int
        get() = mColor
        set(color) {
            this.mColor = color
            this.mPaint!!.color = mColor
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //System.out.println("width = "+getMeasuredWidth()+"     height = "+getMeasuredHeight());
        if (paintBit == null) {
            generatorBit()
        }
    }

    private fun generatorBit() {
        paintBit = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        mPaintCanvas = Canvas(paintBit!!)
    }

    private fun init(context: Context) {
        mColor = Color.RED
        bounds = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        mPaint = Paint()
        mPaint!!.isAntiAlias = true

        mPaint!!.color = mColor
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeWidth = 15f

        mEraserPaint = Paint()
        mEraserPaint!!.alpha = 0
        mEraserPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mEraserPaint!!.isAntiAlias = true
        mEraserPaint!!.isDither = true
        mEraserPaint!!.style = Paint.Style.STROKE
        mEraserPaint!!.strokeJoin = Paint.Join.ROUND
        mEraserPaint!!.strokeCap = Paint.Cap.ROUND
        mEraserPaint!!.strokeWidth = 40f
    }

    fun setWidth(width: Float) {
        this.mPaint!!.strokeWidth = width
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (paintBit != null) {
            canvas.drawBitmap(paintBit!!, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var ret = super.onTouchEvent(event)
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                ret = true
                last_x = x
                last_y = y
            }
            MotionEvent.ACTION_MOVE -> {
                ret = true
                if (bounds!!.contains(x, y) && bounds!!.contains(last_x, last_y)) {
                    mPaintCanvas!!.drawLine(last_x, last_y, x, y, if (eraser) mEraserPaint else mPaint)
                }
                last_x = x
                last_y = y

                this.postInvalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> ret = false
        }
        return ret
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (paintBit != null && !paintBit!!.isRecycled) {
            paintBit!!.recycle()
        }
    }

    fun setEraser(eraser: Boolean) {
        this.eraser = eraser
        mPaint!!.color = if (eraser) Color.TRANSPARENT else mColor
    }

    fun reset() {
        if (paintBit != null && !paintBit!!.isRecycled) {
            paintBit!!.recycle()
        }

        generatorBit()
    }

    fun setBounds(bitmapRect: RectF) {
        this.bounds = bitmapRect
    }
}//end class
