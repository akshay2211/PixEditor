package com.fxn.pixeditor.imageeditengine.filter

import android.graphics.Bitmap
import android.os.AsyncTask
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.TaskCallback

class ApplyFilterTask(private val listenerRef: TaskCallback<Bitmap>?, private val srcBitmap: Bitmap) :
    AsyncTask<ImageFilter, Void, Bitmap>() {

    override fun onCancelled() {
        super.onCancelled()
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)
        if (listenerRef != null) {
            listenerRef!!.onTaskDone(result)
        }
    }

    override fun doInBackground(vararg imageFilters: ImageFilter): Bitmap? {
        if (imageFilters != null && imageFilters.size > 0) {
            val imageFilter = imageFilters[0]
            return PhotoProcessing.filterPhoto(srcBitmap, imageFilter)
        }
        return null
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }
}// end inner class