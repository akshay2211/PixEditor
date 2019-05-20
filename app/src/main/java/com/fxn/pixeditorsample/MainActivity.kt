package com.fxn.pixeditorsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.pixeditor.EditOptions
import com.fxn.pixeditor.PixEditor
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val RequestCode: Int = 100
    private val RequestCodeEditor: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Pix.start(this@MainActivity, Options.init().setRequestCode(RequestCode).setCount(5))
        }
    }

    public override fun onActivityResult(requestCode1: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode1, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode1 == RequestCode) {
            val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            PixEditor.start(this@MainActivity, EditOptions.init().apply {
                requestCode = RequestCodeEditor
                selectedlist = returnValue

            })
            /* ImageEditor.Builder(this, returnValue[0])
                 .setStickerAssets("stickers")
                 .open()*/
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
