package com.fxn.pixeditor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.fxn.pixeditor.imageeditengine.AnimationHelper
import com.fxn.pixeditor.imageeditengine.ImageEditActivity
import com.fxn.pixeditor.imageeditengine.ImageEditor
import com.fxn.pixeditor.imageeditengine.PhotoEditorFragment
import com.fxn.pixeditor.imageeditengine.adapters.FilterImageAdapter
import com.fxn.pixeditor.imageeditengine.adapters.PreviewImageAdapter
import com.fxn.pixeditor.imageeditengine.adapters.PreviewViewPagerAdapter
import com.fxn.pixeditor.imageeditengine.filter.GetFiltersTask
import com.fxn.pixeditor.imageeditengine.interfaces.OnSelectionStringListener
import com.fxn.pixeditor.imageeditengine.interfaces.WorkFinish
import com.fxn.pixeditor.imageeditengine.model.BitmapObject
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.*
import com.fxn.pixeditor.imageeditengine.views.PhotoEditorView
import com.fxn.pixeditor.imageeditengine.views.ViewTouchListener
import com.fxn.pixeditor.imageeditengine.views.imagezoom.ImageViewTouch
import kotlinx.android.synthetic.main.activity_pix_editor.*


class PixEditor : AppCompatActivity(), View.OnClickListener, FilterImageAdapter.FilterImageAdapterListener,
    ViewTouchListener, Animation.AnimationListener {

    private lateinit var photoEditorView: PhotoEditorView
    private var currentMode: Int = 0
    private lateinit var selectedFilter: ImageFilter
    private var filterLayoutHeight: Int = 0
    private lateinit var options: EditOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pix_editor)
        Utility.hideTopBar(this)
        options = intent.getSerializableExtra(EDITOPTIONS) as EditOptions
        initialise()
    }

    val listBitmap = ArrayList<BitmapObject>()
    private lateinit var previewViewPagerAdapter: PreviewViewPagerAdapter
    private fun initialise() {
        previewViewPagerAdapter = PreviewViewPagerAdapter(this@PixEditor)
        listBitmap.clear()
        for (s in options.selectedlist) {
            listBitmap.add(BitmapObject(s))
        }
        previewViewPagerAdapter.list.addAll(listBitmap)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainViewPager.adapter = previewViewPagerAdapter
        mainViewPager.offscreenPageLimit = options.selectedlist.size
        if (options.selectedlist.size > 1) {
            editRecyclerView.visibility = View.VISIBLE
            var previewImageAdapter = PreviewImageAdapter(this)
            previewImageAdapter.addImage(options.selectedlist)
            previewImageAdapter.addOnSelectionListener(object : OnSelectionStringListener {
                override fun onClick(Img: String, view: View, position: Int) {
                    Log.e("position", "recycleview $position")
                    if (position == 0) {
                        onBackPressed()
                        return
                    }
                    mainViewPager.currentItem = position
                }
            })

            editRecyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = previewImageAdapter
            }
        }
        crop_btn.setOnClickListener(this)
        stickers_btn.setOnClickListener(this)
        add_text_btn.setOnClickListener(this)
        paint_btn.setOnClickListener(this)
        done_btn.setOnClickListener(this)
        back_iv.setOnClickListener(this)

        filter_list_layout.post {
            filterLayoutHeight = filter_list_layout.height
            filter_list_layout.translationY = filterLayoutHeight.toFloat()
            photoEditorView =
                mainViewPager.getChildAt(mainViewPager.currentItem).findViewById(R.id.photo_editor_view) as PhotoEditorView
            photoEditorView.setOnTouchListener(

                FilterTouchListener(
                    filter_list_layout,
                    filterLayoutHeight.toFloat(),
                    mainViewPager.getChildAt(mainViewPager.currentItem).findViewById(R.id.image_iv) as ImageView,
                    photoEditorView,
                    filter_label,
                    done_btn,
                    mainViewPager
                )
            )
            /*GetFiltersTask(object : TaskCallback<ArrayList<ImageFilter>> {
                override fun onTaskDone(data: ArrayList<ImageFilter>) {
                    val filterImageAdapter = filter_list_rv.adapter as FilterImageAdapter
                    if (filterImageAdapter != null) {
                        filterImageAdapter!!.setData(data)
                        filterImageAdapter!!.notifyDataSetChanged()
                    }
                }
            }, listBitmap[0].mainBitmap!!).execute()*/
            var mainImageView = mainViewPager.getChildAt(0).findViewById(R.id.image_iv) as ImageViewTouch

            photoEditorView.setImageView(
                bottomPaddingView, topPaddingView, mainImageView, delete_view,
                this
            )
        }
        val filterHelper = FilterHelper()
        filter_list_rv.layoutManager =
            LinearLayoutManager(this@PixEditor, LinearLayoutManager.HORIZONTAL, false)
        val filterImageAdapter = FilterImageAdapter(filterHelper.filters, this@PixEditor)
        filter_list_rv.adapter = filterImageAdapter
        //  photoEditorView =   mainViewPager.getChildAt(0).findViewById(R.id.photo_editor_view) as PhotoEditorView

        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                /*  if (scrollcheck){
                  var image_iv = mainViewPager.getChildAt(position).findViewById(R.id.image_iv) as ImageView
                  var photo_editor_view = mainViewPager.getChildAt(position).findViewById(R.id.photo_editor_view) as PhotoEditorView
                  filter_list_layout.animate().translationY(0f)
                  image_iv.animate().scaleY(0.7f)
                  photo_editor_view.animate().scaleY(0.7f)
                  filter_label.animate().alpha(0f)
                  done_btn.animate().alpha(0f)
                      scrollcheck = false
                  }*/
            }

            override fun onPageSelected(position: Int) {
                Log.e("position change", "-->>" + position)
                filter_list_layout.post {
                    filterLayoutHeight = filter_list_layout.height
                    filter_list_layout.translationY = filterLayoutHeight.toFloat()
                    photoEditorView =
                        mainViewPager.getChildAt(position).findViewById(R.id.photo_editor_view) as PhotoEditorView
                    var mainImageView = mainViewPager.getChildAt(position).findViewById(R.id.image_iv) as ImageViewTouch

                    photoEditorView.setOnTouchListener(
                        FilterTouchListener(
                            filter_list_layout,
                            filterLayoutHeight.toFloat(),
                            mainViewPager.getChildAt(position).findViewById(R.id.image_iv) as ImageView,
                            photoEditorView,
                            filter_label,
                            done_btn,
                            mainViewPager
                        )
                    )
                    mainImageView.post {
                        if (listBitmap[position].mainBitmap != null) {
                            var height = majorContainer!!.height
                            val layouparams = topPaddingView.layoutParams
                            layouparams.height = height / 2 - listBitmap[position].mainBitmap!!.height / 2
                            topPaddingView.layoutParams = layouparams
                            val layouparams1 = bottomPaddingView.layoutParams
                            layouparams1.height = height / 2 - listBitmap[position].mainBitmap!!.height / 2
                            bottomPaddingView.layoutParams = layouparams1
                            photoEditorView.setBounds(mainImageView.bitmapRect!!)
                            photoEditorView.setImageView(
                                bottomPaddingView, topPaddingView, mainImageView, delete_view,
                                this@PixEditor
                            )
                        }
                }
                    GetFiltersTask(object : TaskCallback<ArrayList<ImageFilter>> {
                        override fun onTaskDone(data: ArrayList<ImageFilter>) {
                            val filterImageAdapter = filter_list_rv.adapter as FilterImageAdapter
                            if (filterImageAdapter != null) {
                                filterImageAdapter!!.setData(data)
                                filterImageAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }, listBitmap[position].mainBitmap!!).execute()
                }
            }
        })
    }

    companion object {
        val MODE_NONE = 0
        val MODE_PAINT = 1
        val MODE_ADD_TEXT = 2
        val MODE_STICKER = 3
        var EDITOPTIONS = "EDITOPTIONS"
        fun start(context: Fragment, options: EditOptions) {
            PermUtil.checkForCamaraWritePermissions(context, object : WorkFinish {
                override fun onWorkFinish(check: Boolean?) {
                    val i = Intent(context.activity, PixEditor::class.java)
                    i.putExtra(EDITOPTIONS, options)
                    context.startActivityForResult(i, options.requestCode)
                }
            })
        }

        fun start(context: FragmentActivity, options: EditOptions) {
            PermUtil.checkForCamaraWritePermissions(context, object : WorkFinish {
                override fun onWorkFinish(check: Boolean?) {
                    val i = Intent(context, PixEditor::class.java)
                    i.putExtra(EDITOPTIONS, options)
                    context.startActivityForResult(i, options.requestCode)
                }
            })
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.stickers_btn) {
            setMode(MODE_STICKER)
        } else if (v.id == R.id.add_text_btn) {
            setMode(MODE_ADD_TEXT)
        } else if (v.id == R.id.paint_btn) {
            setMode(MODE_PAINT)
        } else if (v.id == R.id.back_iv) {
            onBackPressed()
        }
    }

    protected fun setMode(mode: Int) {
        var mode = mode
        if (currentMode != mode) {
            onModeChanged(mode)
        } else {
            mode = MODE_NONE
            onModeChanged(mode)
        }
        this.currentMode = mode
    }

    protected fun onModeChanged(currentMode: Int) {
        Log.i(ImageEditActivity::class.java.simpleName, "CM: $currentMode")
        onStickerMode(currentMode == PhotoEditorFragment.MODE_STICKER)
        onAddTextMode(currentMode == PhotoEditorFragment.MODE_ADD_TEXT)
        onPaintMode(currentMode == PhotoEditorFragment.MODE_PAINT)

        if (currentMode == PhotoEditorFragment.MODE_PAINT || currentMode == PhotoEditorFragment.MODE_ADD_TEXT) {
            AnimationHelper.animate(
                this@PixEditor, color_picker_view, R.anim.slide_in_right, View.VISIBLE,
                this@PixEditor
            )
        } else {
            AnimationHelper.animate(
                this@PixEditor, color_picker_view, R.anim.slide_out_right, View.INVISIBLE,
                this@PixEditor
            )
        }
    }

    override fun onFilterSelected(imageFilter: ImageFilter) {
        /*selectedFilter = imageFilter
        ApplyFilterTask(object : TaskCallback<Bitmap> {
            override fun onTaskDone(data: Bitmap) {
                if (data != null) {
                    setImageBitmap(data)
                }
            }
        }, Bitmap.createBitmap(mainBitmap!!)).execute(imageFilter)*/

    }

    private fun onAddTextMode(status: Boolean) {
        if (status) {
            add_text_btn.background =
                Utility.tintDrawable(this@PixEditor, R.drawable.circle2, Color.parseColor("#03A9F4"))
            //photoEditorView.setTextColor(photoEditorView.getColor());
            photoEditorView.addText()
            mainViewPager.scrollerEnabled = false

        } else {
            add_text_btn.background = null
            photoEditorView.hideTextMode()
            mainViewPager.scrollerEnabled = true

        }
    }

    private fun onPaintMode(status: Boolean) {
        if (status) {
            paint_btn.background = Utility.tintDrawable(this@PixEditor, R.drawable.circle2, Color.parseColor("#03A9F4"))
            photoEditorView.showPaintView()
            //paintEditView.setVisibility(View.VISIBLE);
            mainViewPager.scrollerEnabled = false
        } else {
            mainViewPager.scrollerEnabled = true
            paint_btn.background = null
            photoEditorView.hidePaintView()
            //photoEditorView.enableTouch(true);
            //paintEditView.setVisibility(View.GONE);
        }
    }

    private fun onStickerMode(status: Boolean) {
        if (status) {
            stickers_btn.background =
                Utility.tintDrawable(this@PixEditor, R.drawable.circle2, Color.parseColor("#03A9F4"))
            if (this@PixEditor != null && this@PixEditor!!.intent != null) {
                var folderName = this@PixEditor!!.intent.getStringExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME)
                folderName = "stickers"
                photoEditorView.showStickers(folderName)
            }
            mainViewPager.scrollerEnabled = false
        } else {
            mainViewPager.scrollerEnabled = true
            stickers_btn.background = null
            photoEditorView.hideStickers()
        }
    }

    override fun onStartViewChangeListener(view: View) {
        Log.i(ImageEditActivity::class.java.simpleName, "onStartViewChangeListener" + "" + view.id)
        toolbar_layout.visibility = View.GONE
        AnimationHelper.animate(this@PixEditor, delete_view, R.anim.fade_in_medium, View.VISIBLE, this@PixEditor)
    }

    override fun onStopViewChangeListener(view: View) {
        Log.i(ImageEditActivity::class.java.simpleName, "onStopViewChangeListener" + "" + view.id)
        delete_view.visibility = View.GONE
        AnimationHelper.animate(this@PixEditor, toolbar_layout, R.anim.fade_in_medium, View.VISIBLE, this@PixEditor)
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }

}
