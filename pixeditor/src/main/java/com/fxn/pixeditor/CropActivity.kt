package com.fxn.pixeditor

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pixeditor.imageeditengine.cropper.CropImageView
import com.fxn.pixeditor.imageeditengine.utils.Utility
import java.io.File

class CropActivity : AppCompatActivity() {
    private var cropImageView: CropImageView? = null
    private var photoOrignal: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.hideTopBar(this)
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


            val i = Intent()
            i.putExtra("cropdata", cropImageView!!.croppedImage)
            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }

    companion object {
        var CROP_STRING = "crop_string"
        var CROP_NUM = 2121
    }
}
