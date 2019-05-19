package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Sine : Easing {

    override fun easeOut(t: Double, b: Double, c: Double, d: Double): Double {
        return c * Math.sin(t / d * (Math.PI / 2)) + b
    }

    override fun easeIn(t: Double, b: Double, c: Double, d: Double): Double {
        return -c * Math.cos(t / d * (Math.PI / 2)) + c + b
    }

    override fun easeInOut(t: Double, b: Double, c: Double, d: Double): Double {
        return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b
    }

}
