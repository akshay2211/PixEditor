package com.fxn.pixeditor.imageeditengine.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.fxn.pixeditor.R

/**
 * Created by Mark on 11/08/2016.
 */

class VerticalSlideColorPicker : View {

    var defaultColor: Int = 0
        private set
    private var paint: Paint? = null
    private var strokePaint: Paint? = null
    private var path: Path? = null
    private var bitmap: Bitmap? = null
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var centerX: Int = 0
    private var colorPickerRadius: Float = 0.toFloat()
    private var onColorChangeListener: OnColorChangeListener? = null
    private var colorPickerBody: RectF? = null
    private var selectorYPos: Float = 0.toFloat()
    private var borderColor: Int = 0
    private var borderWidth: Float = 0.toFloat()
    private var colors: IntArray? = null
    private var cacheBitmap = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme
            .obtainStyledAttributes(attrs, R.styleable.VerticalSlideColorPicker, 0, 0)

        try {
            borderColor = a.getColor(R.styleable.VerticalSlideColorPicker_borderColor, Color.WHITE)
            defaultColor = a.getColor(R.styleable.VerticalSlideColorPicker_defaultColor, Color.TRANSPARENT)
            borderWidth = a.getDimension(R.styleable.VerticalSlideColorPicker_borderWidth, 5f)
            val colorsResourceId = a.getResourceId(R.styleable.VerticalSlideColorPicker_colors, R.array.default_colors)
            colors = a.resources.getIntArray(colorsResourceId)
        } finally {
            a.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        paint = Paint()
        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true

        path = Path()

        strokePaint = Paint()
        strokePaint!!.style = Paint.Style.STROKE
        strokePaint!!.color = borderColor
        strokePaint!!.isAntiAlias = true
        strokePaint!!.strokeWidth = borderWidth

        isDrawingCacheEnabled = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        path!!.addCircle(centerX.toFloat(), borderWidth + colorPickerRadius, colorPickerRadius, Path.Direction.CW)
        path!!.addRect(colorPickerBody, Path.Direction.CW)
        path!!.addCircle(
            centerX.toFloat(), viewHeight - (borderWidth + colorPickerRadius), colorPickerRadius,
            Path.Direction.CW
        )

        canvas.drawPath(path!!, strokePaint!!)
        canvas.drawPath(path!!, paint!!)

        if (cacheBitmap) {
            bitmap = drawingCache
            cacheBitmap = false
            invalidate()
        } else {
            //canvas.drawLine(colorPickerBody.left, selectorYPos, colorPickerBody.right, selectorYPos, strokePaint);
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var yPos = Math.min(event.y, colorPickerBody!!.bottom)
        yPos = Math.max(colorPickerBody!!.top, yPos)

        selectorYPos = yPos
        defaultColor = bitmap!!.getPixel(viewWidth / 2, selectorYPos.toInt())

        if (onColorChangeListener != null) {
            onColorChangeListener!!.onColorChange(defaultColor)
        }

        //invalidate();

        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w
        viewHeight = h

        centerX = viewWidth / 2
        colorPickerRadius = viewWidth / 2 - borderWidth

        colorPickerBody = RectF(
            centerX - colorPickerRadius, borderWidth + colorPickerRadius,
            centerX + colorPickerRadius, viewHeight - (borderWidth + colorPickerRadius)
        )

        val gradient = LinearGradient(
            0f, colorPickerBody!!.top, 0f, colorPickerBody!!.bottom, colors!!, null,
            Shader.TileMode.CLAMP
        )
        paint!!.shader = gradient

        resetToDefault()
    }

    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        invalidate()
    }

    fun setBorderWidth(borderWidth: Float) {
        this.borderWidth = borderWidth
        invalidate()
    }

    fun setColors(colors: IntArray) {
        this.colors = colors
        cacheBitmap = true
        invalidate()
    }

    fun resetToDefault() {
        selectorYPos = borderWidth + colorPickerRadius

        if (onColorChangeListener != null) {
            onColorChangeListener!!.onColorChange(defaultColor)
        }

        invalidate()
    }

    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener?) {
        this.onColorChangeListener = onColorChangeListener
        onColorChangeListener?.onColorChange(defaultColor)
    }

    interface OnColorChangeListener {

        fun onColorChange(selectedColor: Int)
    }
}