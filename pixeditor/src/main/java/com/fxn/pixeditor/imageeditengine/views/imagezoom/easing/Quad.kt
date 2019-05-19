package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Quad : Easing {

    override fun easeOut(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d
        return -c * t2 * (t - 2) + b
    }

    override fun easeIn(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d
        return c * t2 * t + b
    }

    override fun easeInOut(t: Double, b: Double, c: Double, d: Double): Double {
        var t = t
        var t2 = t / d / 2
        return if (t2 < 1) c / 2 * t * t + b else -c / 2 * (--t * (t - 2) - 1) + b
    }

}
