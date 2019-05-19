package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Circ : Easing {

    override fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time2 = time / duration - 1.0


        return end * Math.sqrt(1.0 - (time2) * time) + start
    }

    override fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time3 = time / duration
        return -end * (Math.sqrt(1.0 - (time3) * time) - 1.0) + start
    }

    override fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double {
        var time = time
        var time2 = time / duration / 2
        var time3 = time - 2.0
        return if (time2 < 1) -end / 2.0 * (Math.sqrt(1.0 - time * time) - 1.0) + start else end / 2.0 * (Math.sqrt(
            1.0 - time3 * time
        ) + 1.0) + start
    }

}
