// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth;
// inexhaustible as the great rivers.
// When they come to an end;
// they begin again;
// like the days and months;
// they die and are reborn;
// like the four seasons."
//
// - Sun Tsu;
// "The Art of War"

package com.fxn.pixeditor.imageeditengine.cropper

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.TypedValue

/**
 * All the possible options that can be set to customize crop image.<br></br>
 * Initialized with default values.
 */
class CropImageOptions : Parcelable {

    /** The shape of the cropping window.  */
    var cropShape: CropImageView.CropShape

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (in pixels)
     */
    var snapRadius: Float = 0.toFloat()

    /**
     * The radius of the touchable area around the handle. (in pixels)<br></br>
     * We are basing this value off of the recommended 48dp Rhythm.<br></br>
     * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
     */
    var touchRadius: Float = 0.toFloat()

    /** whether the guidelines should be on, off, or only showing when resizing.  */
    var guidelines: CropImageView.Guidelines

    /** The initial scale type of the image in the crop image view  */
    var scaleType: CropImageView.ScaleType

    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    var showCropOverlay: Boolean = false

    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    var showProgressBar: Boolean = false

    /**
     * if auto-zoom functionality is enabled.<br></br>
     * default: true.
     */
    var autoZoomEnabled: Boolean = false

    /** if multi-touch should be enabled on the crop box default: false  */
    var multiTouchEnabled: Boolean = false

    /** The max zoom allowed during cropping.  */
    var maxZoom: Int = 0

    /**
     * The initial crop window padding from image borders in percentage of the cropping image
     * dimensions.
     */
    var initialCropWindowPaddingRatio: Float = 0.toFloat()

    /** whether the width to height aspect ratio should be maintained or free to change.  */
    var fixAspectRatio: Boolean = false

    /** the X value of the aspect ratio.  */
    var aspectRatioX: Int = 0

    /** the Y value of the aspect ratio.  */
    var aspectRatioY: Int = 0

    /** the thickness of the guidelines lines in pixels. (in pixels)  */
    var borderLineThickness: Float = 0.toFloat()

    /** the color of the guidelines lines  */
    var borderLineColor: Int = 0

    /** thickness of the corner line. (in pixels)  */
    var borderCornerThickness: Float = 0.toFloat()

    /** the offset of corner line from crop window border. (in pixels)  */
    var borderCornerOffset: Float = 0.toFloat()

    /** the length of the corner line away from the corner. (in pixels)  */
    var borderCornerLength: Float = 0.toFloat()

    /** the color of the corner line  */
    var borderCornerColor: Int = 0

    /** the thickness of the guidelines lines. (in pixels)  */
    var guidelinesThickness: Float = 0.toFloat()

    /** the color of the guidelines lines  */
    var guidelinesColor: Int = 0

    /**
     * the color of the overlay background around the crop window cover the image parts not in the
     * crop window.
     */
    var backgroundColor: Int = 0

    /** the min width the crop window is allowed to be. (in pixels)  */
    var minCropWindowWidth: Int = 0

    /** the min height the crop window is allowed to be. (in pixels)  */
    var minCropWindowHeight: Int = 0

    /**
     * the min width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var minCropResultWidth: Int = 0

    /**
     * the min height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var minCropResultHeight: Int = 0

    /**
     * the max width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var maxCropResultWidth: Int = 0

    /**
     * the max height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var maxCropResultHeight: Int = 0

    /** the title of the [CropImageActivity]  */
    var activityTitle: CharSequence

    /** the color to use for action bar items icons  */
    var activityMenuIconColor: Int = 0

    /** the Android Uri to save the cropped image to  */
    var outputUri: Uri? = null

    /** the compression format to use when writing the image  */
    var outputCompressFormat: Bitmap.CompressFormat

    /** the quality (if applicable) to use when writing the image (0 - 100)  */
    var outputCompressQuality: Int = 0

    /** the width to resize the cropped image to (see options)  */
    var outputRequestWidth: Int = 0

    /** the height to resize the cropped image to (see options)  */
    var outputRequestHeight: Int = 0

    /** the resize method to use on the cropped bitmap (see options documentation)  */
    var outputRequestSizeOptions: CropImageView.RequestSizeOptions

    /** if the result of crop image activity should not save the cropped image bitmap  */
    var noOutputImage: Boolean = false

    /** the initial rectangle to set on the cropping image after loading  */
    var initialCropWindowRectangle: Rect? = null

    /** the initial rotation to set on the cropping image after loading (0-360 degrees clockwise)  */
    var initialRotation: Int = 0

    /** if to allow (all) rotation during cropping (activity)  */
    var allowRotation: Boolean = false

    /** if to allow (all) flipping during cropping (activity)  */
    var allowFlipping: Boolean = false

    /** if to allow counter-clockwise rotation during cropping (activity)  */
    var allowCounterRotation: Boolean = false

    /** the amount of degrees to rotate clockwise or counter-clockwise  */
    var rotationDegrees: Int = 0

    /** whether the image should be flipped horizontally  */
    var flipHorizontally: Boolean = false

    /** whether the image should be flipped vertically  */
    var flipVertically: Boolean = false

    /** optional, the text of the crop menu crop button  */
    var cropMenuCropButtonTitle: CharSequence? = null

    /** optional image resource to be used for crop menu crop icon instead of text  */
    var cropMenuCropButtonIcon: Int = 0

    /** Init options with defaults.  */
    constructor() {

        val dm = Resources.getSystem().displayMetrics

        cropShape = CropImageView.CropShape.RECTANGLE
        snapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        touchRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, dm)
        guidelines = CropImageView.Guidelines.ON_TOUCH
        scaleType = CropImageView.ScaleType.FIT_CENTER
        showCropOverlay = true
        showProgressBar = true
        autoZoomEnabled = true
        multiTouchEnabled = false
        maxZoom = 4
        initialCropWindowPaddingRatio = 0.1f

        fixAspectRatio = false
        aspectRatioX = 1
        aspectRatioY = 1

        borderLineThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        borderLineColor = Color.argb(170, 255, 255, 255)
        borderCornerThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, dm)
        borderCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, dm)
        borderCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, dm)
        borderCornerColor = Color.WHITE

        guidelinesThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm)
        guidelinesColor = Color.argb(170, 255, 255, 255)
        backgroundColor = Color.argb(119, 0, 0, 0)

        minCropWindowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropWindowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropResultWidth = 40
        minCropResultHeight = 40
        maxCropResultWidth = 99999
        maxCropResultHeight = 99999

        activityTitle = ""
        activityMenuIconColor = 0

        outputUri = Uri.EMPTY
        outputCompressFormat = Bitmap.CompressFormat.JPEG
        outputCompressQuality = 90
        outputRequestWidth = 0
        outputRequestHeight = 0
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.NONE
        noOutputImage = false

        initialCropWindowRectangle = null
        initialRotation = -1
        allowRotation = true
        allowFlipping = true
        allowCounterRotation = false
        rotationDegrees = 90
        flipHorizontally = false
        flipVertically = false
        cropMenuCropButtonTitle = null

        cropMenuCropButtonIcon = 0
    }

    /** Create object from parcel.  */
    protected constructor(`in`: Parcel) {
        cropShape = CropImageView.CropShape.values()[`in`.readInt()]
        snapRadius = `in`.readFloat()
        touchRadius = `in`.readFloat()
        guidelines = CropImageView.Guidelines.values()[`in`.readInt()]
        scaleType = CropImageView.ScaleType.values()[`in`.readInt()]
        showCropOverlay = `in`.readByte().toInt() != 0
        showProgressBar = `in`.readByte().toInt() != 0
        autoZoomEnabled = `in`.readByte().toInt() != 0
        multiTouchEnabled = `in`.readByte().toInt() != 0
        maxZoom = `in`.readInt()
        initialCropWindowPaddingRatio = `in`.readFloat()
        fixAspectRatio = `in`.readByte().toInt() != 0
        aspectRatioX = `in`.readInt()
        aspectRatioY = `in`.readInt()
        borderLineThickness = `in`.readFloat()
        borderLineColor = `in`.readInt()
        borderCornerThickness = `in`.readFloat()
        borderCornerOffset = `in`.readFloat()
        borderCornerLength = `in`.readFloat()
        borderCornerColor = `in`.readInt()
        guidelinesThickness = `in`.readFloat()
        guidelinesColor = `in`.readInt()
        backgroundColor = `in`.readInt()
        minCropWindowWidth = `in`.readInt()
        minCropWindowHeight = `in`.readInt()
        minCropResultWidth = `in`.readInt()
        minCropResultHeight = `in`.readInt()
        maxCropResultWidth = `in`.readInt()
        maxCropResultHeight = `in`.readInt()
        activityTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        activityMenuIconColor = `in`.readInt()
        outputUri = `in`.readParcelable(Uri::class.java.classLoader)
        outputCompressFormat = Bitmap.CompressFormat.valueOf(`in`.readString())
        outputCompressQuality = `in`.readInt()
        outputRequestWidth = `in`.readInt()
        outputRequestHeight = `in`.readInt()
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.values()[`in`.readInt()]
        noOutputImage = `in`.readByte().toInt() != 0
        initialCropWindowRectangle = `in`.readParcelable(Rect::class.java.classLoader)
        initialRotation = `in`.readInt()
        allowRotation = `in`.readByte().toInt() != 0
        allowFlipping = `in`.readByte().toInt() != 0
        allowCounterRotation = `in`.readByte().toInt() != 0
        rotationDegrees = `in`.readInt()
        flipHorizontally = `in`.readByte().toInt() != 0
        flipVertically = `in`.readByte().toInt() != 0
        cropMenuCropButtonTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        cropMenuCropButtonIcon = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(cropShape.ordinal)
        dest.writeFloat(snapRadius)
        dest.writeFloat(touchRadius)
        dest.writeInt(guidelines.ordinal)
        dest.writeInt(scaleType.ordinal)
        dest.writeByte((if (showCropOverlay) 1 else 0).toByte())
        dest.writeByte((if (showProgressBar) 1 else 0).toByte())
        dest.writeByte((if (autoZoomEnabled) 1 else 0).toByte())
        dest.writeByte((if (multiTouchEnabled) 1 else 0).toByte())
        dest.writeInt(maxZoom)
        dest.writeFloat(initialCropWindowPaddingRatio)
        dest.writeByte((if (fixAspectRatio) 1 else 0).toByte())
        dest.writeInt(aspectRatioX)
        dest.writeInt(aspectRatioY)
        dest.writeFloat(borderLineThickness)
        dest.writeInt(borderLineColor)
        dest.writeFloat(borderCornerThickness)
        dest.writeFloat(borderCornerOffset)
        dest.writeFloat(borderCornerLength)
        dest.writeInt(borderCornerColor)
        dest.writeFloat(guidelinesThickness)
        dest.writeInt(guidelinesColor)
        dest.writeInt(backgroundColor)
        dest.writeInt(minCropWindowWidth)
        dest.writeInt(minCropWindowHeight)
        dest.writeInt(minCropResultWidth)
        dest.writeInt(minCropResultHeight)
        dest.writeInt(maxCropResultWidth)
        dest.writeInt(maxCropResultHeight)
        TextUtils.writeToParcel(activityTitle, dest, flags)
        dest.writeInt(activityMenuIconColor)
        dest.writeParcelable(outputUri, flags)
        dest.writeString(outputCompressFormat.name)
        dest.writeInt(outputCompressQuality)
        dest.writeInt(outputRequestWidth)
        dest.writeInt(outputRequestHeight)
        dest.writeInt(outputRequestSizeOptions.ordinal)
        dest.writeInt(if (noOutputImage) 1 else 0)
        dest.writeParcelable(initialCropWindowRectangle, flags)
        dest.writeInt(initialRotation)
        dest.writeByte((if (allowRotation) 1 else 0).toByte())
        dest.writeByte((if (allowFlipping) 1 else 0).toByte())
        dest.writeByte((if (allowCounterRotation) 1 else 0).toByte())
        dest.writeInt(rotationDegrees)
        dest.writeByte((if (flipHorizontally) 1 else 0).toByte())
        dest.writeByte((if (flipVertically) 1 else 0).toByte())
        TextUtils.writeToParcel(cropMenuCropButtonTitle, dest, flags)
        dest.writeInt(cropMenuCropButtonIcon)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Validate all the options are withing valid range.
     *
     * @throws IllegalArgumentException if any of the options is not valid
     */
    fun validate() {
        if (maxZoom < 0) {
            throw IllegalArgumentException("Cannot set max zoom to a number < 1")
        }
        if (touchRadius < 0) {
            throw IllegalArgumentException("Cannot set touch radius value to a number <= 0 ")
        }
        if (initialCropWindowPaddingRatio < 0 || initialCropWindowPaddingRatio >= 0.5) {
            throw IllegalArgumentException(
                "Cannot set initial crop window padding value to a number < 0 or >= 0.5"
            )
        }
        if (aspectRatioX <= 0) {
            throw IllegalArgumentException(
                "Cannot set aspect ratio value to a number less than or equal to 0."
            )
        }
        if (aspectRatioY <= 0) {
            throw IllegalArgumentException(
                "Cannot set aspect ratio value to a number less than or equal to 0."
            )
        }
        if (borderLineThickness < 0) {
            throw IllegalArgumentException(
                "Cannot set line thickness value to a number less than 0."
            )
        }
        if (borderCornerThickness < 0) {
            throw IllegalArgumentException(
                "Cannot set corner thickness value to a number less than 0."
            )
        }
        if (guidelinesThickness < 0) {
            throw IllegalArgumentException(
                "Cannot set guidelines thickness value to a number less than 0."
            )
        }
        if (minCropWindowHeight < 0) {
            throw IllegalArgumentException(
                "Cannot set min crop window height value to a number < 0 "
            )
        }
        if (minCropResultWidth < 0) {
            throw IllegalArgumentException("Cannot set min crop result width value to a number < 0 ")
        }
        if (minCropResultHeight < 0) {
            throw IllegalArgumentException(
                "Cannot set min crop result height value to a number < 0 "
            )
        }
        if (maxCropResultWidth < minCropResultWidth) {
            throw IllegalArgumentException(
                "Cannot set max crop result width to smaller value than min crop result width"
            )
        }
        if (maxCropResultHeight < minCropResultHeight) {
            throw IllegalArgumentException(
                "Cannot set max crop result height to smaller value than min crop result height"
            )
        }
        if (outputRequestWidth < 0) {
            throw IllegalArgumentException("Cannot set request width value to a number < 0 ")
        }
        if (outputRequestHeight < 0) {
            throw IllegalArgumentException("Cannot set request height value to a number < 0 ")
        }
        if (rotationDegrees < 0 || rotationDegrees > 360) {
            throw IllegalArgumentException(
                "Cannot set rotation degrees value to a number < 0 or > 360"
            )
        }
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<CropImageOptions> = object : Parcelable.Creator<CropImageOptions> {
            override fun createFromParcel(`in`: Parcel): CropImageOptions {
                return CropImageOptions(`in`)
            }

            override fun newArray(size: Int): Array<CropImageOptions?> {
                return arrayOfNulls(size)
            }

        }
    }
}
