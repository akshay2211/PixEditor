package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Back : Easing {

    override fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double {
        return easeOut(time, start, end, duration, 0.0)
    }

    override fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double {
        return easeIn(time, start, end, duration, 0.0)
    }

    override fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double {
        return easeInOut(time, start, end, duration, 0.9)
    }

    fun easeIn(t: Double, b: Double, c: Double, d: Double, s: Double): Double {
        var t = t
        var s = s
        var q = t / d
        if (s == 0.0) s = 1.70158
        return c * q * t * ((s + 1) * t - s) + b
    }

    fun easeOut(t: Double, b: Double, c: Double, d: Double, s: Double): Double {
        var t = t
        var s = s
        var q = t / d - 1

        if (s == 0.0) s = 1.70158
        return c * ((q) * t * ((s + 1) * t + s) + 1) + b
    }

    fun easeInOut(t: Double, b: Double, c: Double, d: Double, s: Double): Double {
        var t = t
        var s = s
        if (s == 0.0) s = 1.70158
        var q = t / d / 2
        var p = (s * 1.525)
        var o = t - 2.0
        var n = (s * 1.525)
        return if (q < 1) c / 2 * (t * t * ((p + 1) * t - s)) + b else c / 2 * (o * t * ((n + 1) * t + s) + 2) + b
    }
}
