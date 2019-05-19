package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Expo : Easing {

    override fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double {
        return if (time == duration) start + end else end * (-Math.pow(2.0, -10.0 * time / duration) + 1) + start
    }

    override fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double {
        return if (time == 0.0) start else end * Math.pow(2.0, 10.0 * (time / duration - 1.0)) + start
    }

    override fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time2 = time / duration / 2.0
        if (time == 0.0) return start
        if (time == duration) return start + end
        return if ((time2) < 1.0) end / 2.0 * Math.pow(
            2.0,
            10.0 * (time - 1.0)
        ) + start else end / 2.0 * (-Math.pow(2.0, -10.0 * --time) + 2.0) + start
    }

}
