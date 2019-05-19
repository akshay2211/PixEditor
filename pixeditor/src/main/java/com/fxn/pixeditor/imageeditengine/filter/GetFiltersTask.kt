package com.fxn.pixeditor.imageeditengine.filter

import android.graphics.Bitmap
import android.os.AsyncTask
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.FilterHelper
import com.fxn.pixeditor.imageeditengine.utils.TaskCallback
import java.util.*

class GetFiltersTask(private val listenerRef: TaskCallback<ArrayList<ImageFilter>>?, private val srcBitmap: Bitmap) :
    AsyncTask<Void, Void, ArrayList<ImageFilter>>() {

    override fun onCancelled() {
        super.onCancelled()
    }

    override fun onPostExecute(result: ArrayList<ImageFilter>) {
        super.onPostExecute(result)
        if (listenerRef != null) {
            listenerRef!!.onTaskDone(result)
        }
    }

    override fun doInBackground(vararg params: Void): ArrayList<ImageFilter> {
        val filterHelper = FilterHelper()
        val filters = filterHelper.filters
        for (index in filters.indices) {
            val imageFilter = filters[index]
            imageFilter.filterImage = PhotoProcessing.filterPhoto(getScaledBitmap(srcBitmap), imageFilter)
        }
        return filters
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    private fun getScaledBitmap(srcBitmap: Bitmap): Bitmap {
        // Determine how much to scale down the image
        val srcWidth = srcBitmap.width
        val srcHeight = srcBitmap.height

        val targetWidth = 320
        val targetHeight = 240
        if (srcWidth < targetWidth || srcHeight < targetHeight) {
            return srcBitmap
        }

        val scaleFactor = Math.max(
            srcWidth.toFloat() / targetWidth,
            srcHeight.toFloat() / targetHeight
        )

        return Bitmap.createScaledBitmap(
            srcBitmap,
            (srcWidth / scaleFactor).toInt(),
            (srcHeight / scaleFactor).toInt(),
            true
        )
    }
}// end inner class