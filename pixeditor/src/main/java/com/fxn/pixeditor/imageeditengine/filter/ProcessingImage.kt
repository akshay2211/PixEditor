package com.fxn.pixeditor.imageeditengine.filter

import android.graphics.Bitmap
import android.os.AsyncTask
import com.fxn.pixeditor.imageeditengine.utils.TaskCallback
import com.fxn.pixeditor.imageeditengine.utils.Utility

class ProcessingImage(
    private val srcBitmap: Bitmap,
    private val imagePath: String,
    private val callback: TaskCallback<String>?
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg voids: Void): String? {
        return Utility.saveBitmap(srcBitmap, imagePath)
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        if (callback != null) {
            callback!!.onTaskDone(s)
        }
    }
}// end inner class