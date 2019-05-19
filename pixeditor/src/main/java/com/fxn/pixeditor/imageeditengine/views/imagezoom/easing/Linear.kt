package com.fxn.pixeditor.imageeditengine.views.imagezoom.easing

class Linear : Easing {

    fun easeNone(time: Double, start: Double, end: Double, duration: Double): Double {
        return end * time / duration + start
    }

    override fun easeOut(time: Double, start: Double, end: Double, duration: Double): Double {
        return end * time / duration + start
    }

    override fun easeIn(time: Double, start: Double, end: Double, duration: Double): Double {
        return end * time / duration + start
    }

    override fun easeInOut(time: Double, start: Double, end: Double, duration: Double): Double {
        return end * time / duration + start
    }

}
