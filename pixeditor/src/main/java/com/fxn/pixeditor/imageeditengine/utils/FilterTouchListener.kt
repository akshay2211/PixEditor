package com.fxn.pixeditor.imageeditengine.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.fxn.pixeditor.imageeditengine.views.CustomViewPager
import com.fxn.pixeditor.imageeditengine.views.PhotoEditorView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * @author Simon Lightfoot <simon></simon>@demondevelopers.com>
 */
class FilterTouchListener(
    private val mView: View,
    private val viewHeight: Float,
    private val mainImageView: ImageView,
    private val photoEditorView: PhotoEditorView,
    private val filterLabel: View,
    private val doneBtn: FloatingActionButton,
    private val mainViewPager: CustomViewPager
) : View.OnTouchListener {
    private val screenHeight: Int
    private var mMotionDownY: Float = 0.toFloat()

    init {
        val displayMetrics = DisplayMetrics()
        (mainImageView.context as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
    }

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        val action = e.action
        var yPost = 0f
        when (action and e.actionMasked) {
            MotionEvent.ACTION_DOWN -> mMotionDownY = e.rawY - mView.translationY
            MotionEvent.ACTION_MOVE -> {

                yPost = e.rawY - mMotionDownY
                Log.i(
                    FilterTouchListener::class.java.simpleName, (1f - Math.abs(yPost) / 1000).toString()
                            + "--"
                            + yPost
                            + " - "
                            + viewHeight
                            + " - "
                            + mView.y
                            + "s - "
                            + screenHeight
                            + " d="
                            + (screenHeight - mView.y)
                )
                if (yPost >= 0 && yPost < viewHeight) {
                    mView.translationY = yPost
                    filterLabel.alpha = Math.abs(yPost) / 1000
                    doneBtn.alpha = Math.abs(yPost) / 1000
                    //mainImageView.setScaleX(1f-Math.abs(yPost)/1000);
                    //mainImageView.setScaleY(1f-Math.abs(yPost)/1000);
                    Log.i(FilterTouchListener::class.java.simpleName, "moved")
                }
            }
            MotionEvent.ACTION_CANCEL -> Log.i(FilterTouchListener::class.java.simpleName, "ACTION_CANCEL")
            MotionEvent.ACTION_UP -> {

                yPost = e.rawY - mMotionDownY
                val middle = viewHeight / 2
                val diff = screenHeight - mView.y
                Log.e(FilterTouchListener::class.java.simpleName, "ACTION_UP$yPost $diff $middle")
                if (diff < middle) {
                    photoEditorView.onStopViewFullChangeListener(v)
                    mainViewPager.scrollerEnabled = true
                    mView.animate().translationY(viewHeight)
                    mainImageView.animate().scaleX(1f)
                    mainImageView.animate().scaleY(1f)
                    photoEditorView.animate().scaleX(1f)
                    photoEditorView.animate().scaleY(1f)
                    filterLabel.animate().alpha(1f)
                    doneBtn.animate().alpha(1f)
                } else {
                    photoEditorView.onStartViewFullChangeListener(v)
                    mainViewPager.scrollerEnabled = false
                    mView.animate().translationY(0f)
                    mainImageView.animate().scaleX(0.7f)
                    mainImageView.animate().scaleY(0.7f)
                    photoEditorView.animate().scaleX(0.7f)
                    photoEditorView.animate().scaleY(0.7f)
                    filterLabel.animate().alpha(0f)
                    doneBtn.animate().alpha(0f)
                }
            }
        }
        return true
    }
}