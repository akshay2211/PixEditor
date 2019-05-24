package com.fxn.pixeditor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pixeditor.imageeditengine.cropper.CropImageView
import com.fxn.pixeditor.imageeditengine.utils.Utility
import java.io.ByteArrayOutputStream
import java.io.File


class CropActivity : AppCompatActivity() {
    private var cropImageView: CropImageView? = null
    private var photoOrignal: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utility.hideTopBar(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        /* Utility.setupStatusBarHidden(this);*/
        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cropImageView = findViewById(R.id.cropImageView)
        val path = intent.getStringExtra(CROP_STRING)
        photoOrignal = File(path)
        cropImageView!!.setImageBitmap(BitmapFactory.decodeFile(path))
        findViewById<View>(R.id.cancel).setOnClickListener { finish() }
        findViewById<View>(R.id.rotate).setOnClickListener { cropImageView!!.rotateImage(90) }
        findViewById<View>(R.id.done).setOnClickListener {
            val stream = ByteArrayOutputStream()
            val bmp = cropImageView!!.croppedImage
            bmp!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bytes = stream.toByteArray()

            val i = Intent()
            i.putExtra("cropdata", bytes)
            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }

    companion object {
        var CROP_STRING = "crop_string"
        var CROP_NUM = 2121
    }
}
