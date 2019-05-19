package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Cubic : Easing {

    override fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time2 = time / duration - 1.0
        return end * (time2 * time * time + 1.0) + start
    }

    override fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time3 = time / duration
        return end * time3 * time * time + start
    }

    override fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time3 = time / duration / 2.0
        var time2 = time - 2.0
        return if (time3 < 1.0) end / 2.0 * time * time * time + start else end / 2.0 * (time2 * time * time + 2.0) + start
    }
}
