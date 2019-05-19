package com.fxn.pixeditor.imageeditengine.utils

interface TaskCallback<T> {
    fun onTaskDone(data: T)
}
