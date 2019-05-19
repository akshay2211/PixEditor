package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

interface Easing {

    fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double

    fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double

    fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double
}
