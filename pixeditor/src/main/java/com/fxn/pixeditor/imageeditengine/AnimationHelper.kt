package com.fxn.pixeditor.imageeditengine

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes


/**
 * Created by droidNinja on 03/06/16.
 */
object AnimationHelper {
    fun animate(
        context: Context,
        view: View, @AnimRes anim: Int,
        visibility: Int,
        animationListener: Animation.AnimationListener
    ) {
        if (view.visibility != visibility) {
            val animation = AnimationUtils.loadAnimation(context, anim)
            animation.setAnimationListener(animationListener)

            view.startAnimation(animation)
            view.visibility = visibility
        }
    }
}
