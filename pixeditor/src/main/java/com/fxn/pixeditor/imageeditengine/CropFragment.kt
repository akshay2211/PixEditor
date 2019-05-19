package com.fxn.pixeditor.imageeditengine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.views.cropimage.CropImageView
import kotlinx.android.synthetic.main.fragment_crop.view.*

class CropFragment : BaseFragment(), View.OnClickListener {

    private var mListener: OnFragmentInteractionListener? = null
    private var cropImageView: CropImageView? = null
    private val currentAngle: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crop, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun setImageBitmap(bitmap: Bitmap) {
        cropImageView!!.setImageBitmap(bitmap)
    }

    interface OnFragmentInteractionListener {
        fun onImageCropped(bitmap: Bitmap, cropRect: Rect)
        fun onCancelCrop()
    }

    override fun initView(view: View) {
        cropImageView = view.image_iv
        view.cancel_tv.setOnClickListener(this)
        view.back_iv.setOnClickListener(this)
        view.rotate_iv.setOnClickListener(this)
        view.done_tv.setOnClickListener(this)
        if (arguments != null) {
            val bitmapimage: Bitmap = this@CropFragment.arguments!!.getParcelable(ImageEditor.EXTRA_ORIGINAL)
            if (bitmapimage != null) {
                cropImageView!!.setImageBitmap(bitmapimage)
                cropImageView!!.setAspectRatio(1, 1)
                cropImageView!!.guidelines = CropImageView.Guidelines.ON_TOUCH
                val cropRect: Rect = arguments!!.getParcelable(ImageEditor.EXTRA_CROP_RECT)
                if (cropRect != null) {
                    cropImageView!!.cropRect = cropRect
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.rotate_iv) {
            cropImageView!!.rotateImage(90)
        } else if (view.id == R.id.cancel_tv) {
            mListener!!.onCancelCrop()
        } else if (view.id == R.id.done_tv) {
            val original: Bitmap = arguments!!.getParcelable(ImageEditor.EXTRA_ORIGINAL)
            mListener!!.onImageCropped(cropImageView!!.croppedImage!!, cropImageView!!.cropRect!!)
        } else if (view.id == R.id.done_tv) {
            activity!!.onBackPressed()
        }
    }

    companion object {

        fun newInstance(bitmap: Bitmap, cropRect: Rect): CropFragment {
            val cropFragment = CropFragment()
            val bundle = Bundle()
            bundle.putParcelable(ImageEditor.EXTRA_ORIGINAL, bitmap)
            bundle.putParcelable(ImageEditor.EXTRA_CROP_RECT, cropRect)
            cropFragment.arguments = bundle

            return cropFragment
        }
    }
}// Required empty public constructor
