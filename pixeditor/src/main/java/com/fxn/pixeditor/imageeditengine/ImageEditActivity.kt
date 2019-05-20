package com.fxn.pixeditor.imageeditengine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.utils.FragmentUtil

class ImageEditActivity : BaseImageEditActivity(), PhotoEditorFragment.OnFragmentInteractionListener {
    private val cropRect: Rect? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_edit)
        val window = window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }
        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*    int val = Utility.getSoftButtonsBarSizePort(this);
            val=val/2;*/

        // Log.e("---","---------------------------------"+val);
        /* FrameLayout.LayoutParams lp =
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
    lp.setMargins(0, 0, 0, val);*/
        //((FrameLayout) findViewById(R.id.fragment_container)).setLayoutParams(lp);
        val imagePath = intent.getStringExtra(ImageEditor.EXTRA_IMAGE_PATH)
        if (imagePath != null) {
            val mode = intent.getIntExtra(ImageEditor.EXTRA_START, 0)
            FragmentUtil.addFragment(
                this, R.id.fragment_container,
                PhotoEditorFragment.newInstance(imagePath, "", mode)
            )
            if (mode == PhotoEditorFragment.MODE_ADD_TEXT) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(null, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    override fun onCropClicked(bitmap: Bitmap) {
        /* FragmentUtil.replaceFragment(this, R.id.fragment_container,
        CropFragment.newInstance(bitmap, cropRect));*/
    }

    override fun onDoneClicked(imagePath: String) {

        val intent = Intent()
        intent.putExtra(ImageEditor.EXTRA_EDITED_PATH, imagePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /* @Override
  public void onImageCropped(Bitmap bitmap, Rect cropRect) {
    this.cropRect = cropRect;
    PhotoEditorFragment photoEditorFragment =
        (PhotoEditorFragment) FragmentUtil.getFragmentByTag(this,
            PhotoEditorFragment.class.getSimpleName());
    if (photoEditorFragment != null) {
      photoEditorFragment.setImageWithRect(cropRect);
      photoEditorFragment.reset();
      FragmentUtil.removeFragment(this,
          (BaseFragment) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
    }
  }*/

    /* @Override
  public void onCancelCrop() {
    FragmentUtil.removeFragment(this,
        (BaseFragment) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
  }*/

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    companion object {
        var hasSoftKeys = false
    }
}
