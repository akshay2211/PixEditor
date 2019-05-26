package com.fxn.pixeditorsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.adapters.MyAdapter
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.pixeditor.EditOptions
import com.fxn.pixeditor.PixEditor
import com.fxn.pixeditor.imageeditengine.interfaces.AddMoreImagesListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), AddMoreImagesListener {
    override fun addMore(context: AppCompatActivity, list: ArrayList<String>, requestCodePix: Int) {
        Pix.start(context, Options.init().apply {
            requestCode = requestCodePix
            count = 5
            preSelectedUrls = list
        })
    }

    private lateinit var editOptions: EditOptions
    private val RequestCode: Int = 100
    private val RequestCodeEditor: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MyAdapter(this@MainActivity)
        }
        editOptions = EditOptions.init().apply {
            requestCode = RequestCodeEditor
            addMoreImagesListener = this@MainActivity
            }



        fab.setOnClickListener { view ->
            Pix.start(this@MainActivity, Options.init().setRequestCode(RequestCode).setCount(5))
        }
    }

    public override fun onActivityResult(requestCode1: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode1, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode1 == RequestCode) {
            val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            editOptions.selectedlist = returnValue
            PixEditor.start(this@MainActivity, editOptions)
        }
        if (resultCode == Activity.RESULT_OK && requestCode1 == RequestCodeEditor) {
            val returnValue = data!!.getStringArrayListExtra(PixEditor.IMAGE_RESULTS)
            (recyclerView.adapter as MyAdapter).addImage(returnValue)
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
