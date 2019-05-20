package com.fxn.pixeditor

import android.content.Intent
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
import com.fxn.pixeditor.imageeditengine.adapters.FilterImageAdapter
import com.fxn.pixeditor.imageeditengine.adapters.PreviewImageAdapter
import com.fxn.pixeditor.imageeditengine.adapters.PreviewViewPagerAdapter
import com.fxn.pixeditor.imageeditengine.interfaces.OnSelectionStringListener
import com.fxn.pixeditor.imageeditengine.interfaces.WorkFinish
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.FilterHelper
import com.fxn.pixeditor.imageeditengine.utils.FilterTouchListener
import com.fxn.pixeditor.imageeditengine.utils.PermUtil
import com.fxn.pixeditor.imageeditengine.utils.Utility
import com.fxn.pixeditor.imageeditengine.views.PhotoEditorView
import com.fxn.pixeditor.imageeditengine.views.ViewTouchListener
import kotlinx.android.synthetic.main.activity_pix_editor.*

class PixEditor : AppCompatActivity(), View.OnClickListener, FilterImageAdapter.FilterImageAdapterListener,
    ViewTouchListener, Animation.AnimationListener {


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

    private lateinit var previewViewPagerAdapter: PreviewViewPagerAdapter
    private fun initialise() {
        previewViewPagerAdapter = PreviewViewPagerAdapter(this@PixEditor)
        previewViewPagerAdapter.list.addAll(options.selectedlist)
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
            var photo_editor_view =
                mainViewPager.getChildAt(mainViewPager.currentItem).findViewById(R.id.photo_editor_view) as PhotoEditorView
            photo_editor_view.setOnTouchListener(

                FilterTouchListener(
                    filter_list_layout,
                    filterLayoutHeight.toFloat(),
                    mainViewPager.getChildAt(mainViewPager.currentItem).findViewById(R.id.image_iv) as ImageView,
                    photo_editor_view,
                    filter_label,
                    done_btn,
                    mainViewPager
                )
            )
        }
        val filterHelper = FilterHelper()
        filter_list_rv.layoutManager =
            LinearLayoutManager(this@PixEditor, LinearLayoutManager.HORIZONTAL, false)
        val filterImageAdapter = FilterImageAdapter(filterHelper.filters, this@PixEditor)
        filter_list_rv.adapter = filterImageAdapter
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
                filter_list_layout.post {
                    filterLayoutHeight = filter_list_layout.height
                    filter_list_layout.translationY = filterLayoutHeight.toFloat()
                    var photo_editor_view =
                        mainViewPager.getChildAt(position).findViewById(R.id.photo_editor_view) as PhotoEditorView

                    photo_editor_view.setOnTouchListener(
                        FilterTouchListener(
                            filter_list_layout,
                            filterLayoutHeight.toFloat(),
                            mainViewPager.getChildAt(position).findViewById(R.id.image_iv) as ImageView,
                            photo_editor_view,
                            filter_label,
                            done_btn,
                            mainViewPager
                        )
                    )


                }
            }
        })
    }

    companion object {
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

    override fun onClick(v: View?) {

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

    override fun onStartViewChangeListener(view: View) {

    }

    override fun onStopViewChangeListener(view: View) {
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }

}
