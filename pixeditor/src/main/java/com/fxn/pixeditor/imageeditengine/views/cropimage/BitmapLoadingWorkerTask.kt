// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.fxn.pixeditor.imageeditengine.views.cropimage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask

import java.lang.ref.WeakReference

/** Task to load bitmap asynchronously from the UI thread.  */
internal class BitmapLoadingWorkerTask
// endregion

    (
    cropImageView: CropImageView,
    /** The Android URI of the image to load  */
    /** The Android URI that this task is currently loading.  */
    val uri: Uri
) : AsyncTask<Void, Void, BitmapLoadingWorkerTask.Result>() {

    // region: Fields and Consts

    /** Use a WeakReference to ensure the ImageView can be garbage collected  */
    private val mCropImageViewReference: WeakReference<CropImageView>

    /** The context of the crop image view widget used for loading of bitmap by Android URI  */
    private val mContext: Context

    /** required width of the cropping image after density adjustment  */
    private val mWidth: Int

    /** required height of the cropping image after density adjustment  */
    private val mHeight: Int

    init {
        mCropImageViewReference = WeakReference(cropImageView)

        mContext = cropImageView.context

        val metrics = cropImageView.resources.displayMetrics
        val densityAdj: Float = (if (metrics.density > 1) 1 / metrics.density else 1f)
        mWidth = (metrics.widthPixels * densityAdj).toInt()
        mHeight = (metrics.heightPixels * densityAdj).toInt()
    }

    /**
     * Decode image in background.
     *
     * @param params ignored
     * @return the decoded bitmap data
     */
    override fun doInBackground(vararg params: Void): Result? {
        try {
            if (!isCancelled) {

                val decodeResult = BitmapUtils.decodeSampledBitmap(mContext, uri, mWidth, mHeight)

                if (!isCancelled) {

                    val rotateResult = BitmapUtils.rotateBitmapByExif(decodeResult.bitmap!!, mContext, uri)

                    return Result(
                        uri, rotateResult.bitmap, decodeResult.sampleSize, rotateResult.degrees
                    )
                }
            }
            return null
        } catch (e: Exception) {
            return Result(uri, e)
        }

    }

    /**
     * Once complete, see if ImageView is still around and set bitmap.
     *
     * @param result the result of bitmap loading
     */
    override fun onPostExecute(result: Result?) {
        if (result != null) {
            var completeCalled = false
            if (!isCancelled) {
                val cropImageView = mCropImageViewReference.get()
                if (cropImageView != null) {
                    completeCalled = true
                    cropImageView.onSetImageUriAsyncComplete(result)
                }
            }
            if (!completeCalled && result.bitmap != null) {
                // fast release of unused bitmap
                result.bitmap.recycle()
            }
        }
    }

    // region: Inner class: Result

    /** The result of BitmapLoadingWorkerTask async loading.  */
    class Result {

        /** The Android URI of the image to load  */
        val uri: Uri

        /** The loaded bitmap  */
        val bitmap: Bitmap?

        /** The sample size used to load the given bitmap  */
        val loadSampleSize: Int

        /** The degrees the image was rotated  */
        val degreesRotated: Int

        /** The error that occurred during async bitmap loading.  */
        val error: Exception?

        internal constructor(uri: Uri, bitmap: Bitmap, loadSampleSize: Int, degreesRotated: Int) {
            this.uri = uri
            this.bitmap = bitmap
            this.loadSampleSize = loadSampleSize
            this.degreesRotated = degreesRotated
            this.error = null
        }

        internal constructor(uri: Uri, error: Exception) {
            this.uri = uri
            this.bitmap = null
            this.loadSampleSize = 0
            this.degreesRotated = 0
            this.error = error
        }
    }
    // endregion
}
