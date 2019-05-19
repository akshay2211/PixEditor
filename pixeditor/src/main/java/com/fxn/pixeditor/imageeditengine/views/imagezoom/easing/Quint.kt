package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Quint : Easing {

    override fun easeOut(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d - 1
        return c * (t2 * t * t * t * t + 1) + b
    }

    override fun easeIn(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d
        return c * t2 * t * t * t * t + b
    }

    override fun easeInOut(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d / 2
        var t3 = t - 2.0
        return if (t2 < 1) c / 2 * t * t * t * t * t + b else c / 2 * (t3 * t * t * t * t + 2) + b
    }

}
