package com.fxn.pixeditor.imageeditengine.views.imagezoom.utils

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images

import java.io.IOException

object ExifUtils {

    val EXIF_TAGS = arrayOf(
        "FNumber",
        ExifInterface.TAG_DATETIME,
        "ExposureTime",
        ExifInterface.TAG_FLASH,
        ExifInterface.TAG_FOCAL_LENGTH,
        "GPSAltitude",
        "GPSAltitudeRef",
        ExifInterface.TAG_GPS_DATESTAMP,
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_GPS_TIMESTAMP,
        ExifInterface.TAG_IMAGE_LENGTH,
        ExifInterface.TAG_IMAGE_WIDTH,
        "ISOSpeedRatings",
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_WHITE_BALANCE
    )

    /**
     * Return the rotation of the passed image file
     *
     * @param filepath
     * image absolute file path
     * @return image orientation
     */
    fun getExifOrientation(filepath: String?): Int {
        if (null == filepath)
            return 0
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)
        } catch (e: IOException) {
            return 0
        }

        return getExifOrientation(exif)
    }

    fun getExifOrientation(exif: ExifInterface?): Int {
        var degree = 0

        if (exif != null) {
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1
            )
            if (orientation != -1) {
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            }
        }
        return degree
    }

    /**
     * Load the exif tags into the passed Bundle
     *
     * @param filepath
     * @param out
     * @return true if exif tags are loaded correctly
     */
    fun loadAttributes(filepath: String, out: Bundle): Boolean {
        val e: ExifInterface
        try {
            e = ExifInterface(filepath)
        } catch (e1: IOException) {
            e1.printStackTrace()
            return false
        }

        for (tag in EXIF_TAGS) {
            out.putString(tag, e.getAttribute(tag))
        }
        return true
    }

    /**
     * Store the exif attributes in the passed image file using the TAGS stored
     * in the passed bundle
     *
     * @param filepath
     * @param bundle
     * @return true if success
     */
    fun saveAttributes(filepath: String, bundle: Bundle): Boolean {
        val exif: ExifInterface
        try {
            exif = ExifInterface(filepath)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        for (tag in EXIF_TAGS) {
            if (bundle.containsKey(tag)) {
                exif.setAttribute(tag, bundle.getString(tag))
            }
        }
        try {
            exif.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * Return the string representation of the given orientation
     *
     * @param orientation
     * @return
     */
    fun getExifOrientation(orientation: Int): String {
        when (orientation) {
            0 -> return ExifInterface.ORIENTATION_NORMAL.toString()
            90 -> return ExifInterface.ORIENTATION_ROTATE_90.toString()
            180 -> return ExifInterface.ORIENTATION_ROTATE_180.toString()
            270 -> return ExifInterface.ORIENTATION_ROTATE_270.toString()
            else -> throw AssertionError("invalid: $orientation")
        }
    }

    /**
     * Try to get the exif orientation of the passed image uri
     *
     * @param context
     * @param uri
     * @return
     */
    fun getExifOrientation(context: Context, uri: Uri): Int {

        val scheme = uri.scheme

        var provider: ContentProviderClient? = null
        if (scheme == null || ContentResolver.SCHEME_FILE == scheme) {
            return getExifOrientation(uri.path)
        } else if (scheme == ContentResolver.SCHEME_CONTENT) {
            try {
                provider = context.contentResolver
                    .acquireContentProviderClient(uri)
            } catch (e: SecurityException) {
                return 0
            }

            if (provider != null) {
                val result: Cursor?
                try {
                    result = provider.query(
                        uri,
                        arrayOf(Images.ImageColumns.ORIENTATION, Images.ImageColumns.DATA),
                        null,
                        null,
                        null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    return 0
                }

                if (result == null) {
                    return 0
                }

                val orientationColumnIndex = result
                    .getColumnIndex(Images.ImageColumns.ORIENTATION)
                val dataColumnIndex = result
                    .getColumnIndex(Images.ImageColumns.DATA)

                try {
                    if (result.count > 0) {
                        result.moveToFirst()

                        var rotation = 0

                        if (orientationColumnIndex > -1) {
                            rotation = result.getInt(orientationColumnIndex)
                        }

                        if (dataColumnIndex > -1) {
                            val path = result.getString(dataColumnIndex)
                            rotation = rotation or getExifOrientation(path)
                        }
                        return rotation
                    }
                } finally {
                    result.close()
                }
            }
        }
        return 0
    }
}
