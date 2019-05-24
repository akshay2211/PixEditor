package com.fxn.pixeditor.imageeditengine

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.adapters.FilterImageAdapter
import com.fxn.pixeditor.imageeditengine.filter.ApplyFilterTask
import com.fxn.pixeditor.imageeditengine.filter.GetFiltersTask
import com.fxn.pixeditor.imageeditengine.filter.ProcessingImage
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.*
import com.fxn.pixeditor.imageeditengine.views.PhotoEditorView
import com.fxn.pixeditor.imageeditengine.views.VerticalSlideColorPicker
import com.fxn.pixeditor.imageeditengine.views.ViewTouchListener
import com.fxn.pixeditor.imageeditengine.views.imagezoom.ImageViewTouch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_photo_editor.*
import kotlinx.android.synthetic.main.fragment_photo_editor.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoEditorFragment : BaseFragment(), View.OnClickListener, ViewTouchListener,
    FilterImageAdapter.FilterImageAdapterListener, Animation.AnimationListener {
    override fun onStartViewFullChangeListener(view: View) {
    }

    override fun onStopViewFullChangeListener(view: View) {
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }

    protected var currentMode: Int = 0
    internal lateinit var mainImageView: ImageViewTouch
    internal lateinit var stickerButton: ImageView
    internal lateinit var addTextButton: ImageView
    internal lateinit var photoEditorView: PhotoEditorView
    internal lateinit var paintButton: ImageView
    internal lateinit var cropButton: ImageView
    internal lateinit var deleteButton: ImageView
    internal lateinit var colorPickerView: VerticalSlideColorPicker
    //CustomPaintView paintEditView;
    internal lateinit var toolbarLayout: View
    internal lateinit var filterRecylerview: RecyclerView
    internal lateinit var filterLayout: View
    internal lateinit var filterLabel: View
    internal lateinit var doneBtn: FloatingActionButton
    internal var height: Int = 0
    internal var width: Int = 0
    internal lateinit var bottomPaddingView: ImageView
    internal lateinit var topPaddingView: ImageView
    private var mainBitmap: Bitmap? = null
    private val cacheStack: LruCache<Int, Bitmap>? = null
    private var filterLayoutHeight: Int = 0
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var selectedFilter: ImageFilter
    private var originalBitmap: Bitmap? = null
    private var majorContainer: View? = null
    private var dir: File? = null
    private var photo1: File? = null
    private var photoOrignal: File? = null
    private var optionsImagePath: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_photo_editor, container, false)
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels

        dir = File(Environment.getExternalStorageDirectory(), optionsImagePath)
        photo1 = File(
            dir,
            "IMG_"
                    + SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date())
                    + "_Edited.jpg"
        )

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context!!.toString() + " must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
    fun setImageBitmap(bitmap: Bitmap?) {
        mainImageView.setImageBitmap(bitmap)

        mainImageView.post {
            if (bitmap != null) {
                height = majorContainer!!.height
                val layouparams = topPaddingView.layoutParams
                layouparams.height = height / 2 - bitmap.height / 2
                topPaddingView.layoutParams = layouparams
                val layouparams1 = bottomPaddingView.layoutParams
                layouparams1.height = height / 2 - bitmap.height / 2
                bottomPaddingView.layoutParams = layouparams1
                photoEditorView.setBounds(mainImageView.bitmapRect!!)
            }
        }
    }

    fun setImageWithRect(rect: Rect) {
        mainBitmap = getScaledBitmap(getCroppedBitmap(getBitmapCache(originalBitmap), rect))
        mainImageView.setImageBitmap(mainBitmap)
        mainImageView.post(Runnable { photoEditorView.setBounds(mainImageView.bitmapRect!!) })

        GetFiltersTask(object : TaskCallback<ArrayList<ImageFilter>> {
            override fun onTaskDone(data: ArrayList<ImageFilter>) {
                val filterImageAdapter = filterRecylerview.adapter as FilterImageAdapter
                if (filterImageAdapter != null) {
                    filterImageAdapter.setData(data)
                    filterImageAdapter.notifyDataSetChanged()
                }
            }
        }, mainBitmap!!).execute()
    }

    private fun getScaledBitmap(resource: Bitmap): Bitmap {
        val currentBitmapWidth = resource.width
        val currentBitmapHeight = resource.height
        val ivWidth = mainImageView.width
        val newHeight = Math.floor(
            currentBitmapHeight.toDouble() * (ivWidth.toDouble() / currentBitmapWidth.toDouble())
        ).toInt()
        return Bitmap.createScaledBitmap(resource, ivWidth, newHeight, true)
    }

    private fun getCroppedBitmap(srcBitmap: Bitmap, rect: Rect): Bitmap {
        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(
            srcBitmap,
            rect.left,
            rect.top,
            rect.right - rect.left,
            rect.bottom - rect.top
        )
    }

    fun reset() {
        photoEditorView.reset()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun hasNavBar(context: Context): Boolean {
        val realSize = Point()
        val screenSize = Point()
        var hasNavBar = false
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getRealMetrics(metrics)
        realSize.x = metrics.widthPixels
        realSize.y = metrics.heightPixels
        activity!!.windowManager.defaultDisplay.getSize(screenSize)
        if (realSize.y != screenSize.y) {
            val difference = realSize.y - screenSize.y
            var navBarHeight = 0
            val resources = context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            if (navBarHeight != 0) {
                if (difference == navBarHeight) {
                    hasNavBar = true
                }
            }
        }
        return hasNavBar
    }

    override fun initView(view: View) {
        majorContainer = view.majorContainer
        bottomPaddingView = view.bottomPaddingView
        bottomPaddingView = view.bottomPaddingView
        topPaddingView = view.topPaddingView
        mainImageView = view.image_iv
        stickerButton = view.stickers_btn
        addTextButton = view.add_text_btn
        deleteButton = view.delete_view
        cropButton = view.crop_btn
        photoEditorView = view.photo_editor_view
        paintButton = view.paint_btn
        colorPickerView = view.color_picker_view
        //paintEditView = paint_edit_view;
        toolbarLayout = view.toolbar_layout
        filterRecylerview = view.filter_list_rv
        filterLayout = view.filter_list_layout
        filterLabel = view.filter_label
        doneBtn = view.done_btn

        if (arguments != null && activity != null && activity!!.intent != null) {
            val imagePath = arguments!!.getString(ImageEditor.EXTRA_IMAGE_PATH)
            optionsImagePath = arguments!!.getString("options")
            photoOrignal = File(imagePath)
            //mainImageView.post(new Runnable() {
            //  @Override public void run() {
            //    mainBitmap = Utility.decodeBitmap(imagePath,mainImageView.getWidth(),mainImageView.getHeight());
            //
            //  }
            //});

            // Log.e("imagePath","->"+imagePath);
            Glide.with(this)
                .asBitmap()
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        @NonNull resource: Bitmap,
                        @Nullable transition: Transition<in Bitmap>?
                    ) {
                        val currentBitmapWidth = resource.width
                        val currentBitmapHeight = resource.height
                        mainImageView.invalidate()
                        try {
                            val ivWidth = mainImageView.width
                            val newHeight = Math.floor(
                                currentBitmapHeight.toDouble() * (ivWidth.toDouble() / currentBitmapWidth.toDouble())
                            ).toInt()
                            originalBitmap = Bitmap.createScaledBitmap(resource, ivWidth, newHeight, true)
                            mainBitmap = originalBitmap
                        } catch (e: Exception) {
                            mainBitmap = resource
                        }

                        setImageBitmap(mainBitmap)
                        GetFiltersTask(object : TaskCallback<ArrayList<ImageFilter>> {
                            override fun onTaskDone(data: ArrayList<ImageFilter>) {
                                val filterImageAdapter = filterRecylerview.adapter as FilterImageAdapter
                                if (filterImageAdapter != null) {
                                    filterImageAdapter.setData(data)
                                    filterImageAdapter.notifyDataSetChanged()
                                }
                            }
                        }, mainBitmap!!).execute()
                    }
                })

            val intent = activity!!.intent
            setVisibility(addTextButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_TEXT_MODE, false))
            setVisibility(
                stickerButton,
                intent.getBooleanExtra(ImageEditor.EXTRA_IS_STICKER_MODE, false)
            )
            setVisibility(cropButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_CROP_MODE, false))

            setVisibility(paintButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_PAINT_MODE, false))
            setVisibility(filterLayout, intent.getBooleanExtra(ImageEditor.EXTRA_HAS_FILTERS, false))

            photoEditorView.setImageView(
                bottomPaddingView, topPaddingView, mainImageView, deleteButton,
                this
            )
            //stickerEditorView.setImageView(mainImageView, deleteButton,this);
            cropButton.setOnClickListener(this)
            stickerButton.setOnClickListener(this)
            addTextButton.setOnClickListener(this)
            paintButton.setOnClickListener(this)
            doneBtn.setOnClickListener(this)
            back_iv.setOnClickListener(this)

            colorPickerView.setOnColorChangeListener(
                object : VerticalSlideColorPicker.OnColorChangeListener {
                    override fun onColorChange(selectedColor: Int) {
                        if (currentMode == MODE_PAINT) {
                            paintButton.background = Utility.tintDrawable(context!!, R.drawable.circle2, selectedColor)
                            photoEditorView.color = (selectedColor)
                        } else if (currentMode == MODE_ADD_TEXT) {
                            addTextButton.background =
                                Utility.tintDrawable(context!!, R.drawable.circle2, selectedColor)
                            photoEditorView.setTextColor(selectedColor)
                        }
                    }
                })
            photoEditorView.color = (colorPickerView.defaultColor)
            photoEditorView.setTextColor(colorPickerView.defaultColor)

            if (intent.getBooleanExtra(ImageEditor.EXTRA_HAS_FILTERS, false)) {
                filterLayout.post {
                    filterLayoutHeight = filterLayout.height
                    filterLayout.translationY = filterLayoutHeight.toFloat()
                    photoEditorView.setOnTouchListener(
                        FilterTouchListener(
                            filterLayout, filterLayoutHeight.toFloat(), mainImageView,
                            photoEditorView, filterLabel, doneBtn, null!!
                        )
                    )
                }
                val filterHelper = FilterHelper()
                filterRecylerview.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val filterImageAdapter = FilterImageAdapter(filterHelper.filters, this)
                filterRecylerview.adapter = filterImageAdapter
            }
            setMode(arguments!!.getInt("mode"))
        }
    }

    protected fun onModeChanged(currentMode: Int) {
        Log.i(ImageEditActivity::class.java.simpleName, "CM: $currentMode")
        onStickerMode(currentMode == MODE_STICKER)
        onAddTextMode(currentMode == MODE_ADD_TEXT)
        onPaintMode(currentMode == MODE_PAINT)

        if (currentMode == MODE_PAINT || currentMode == MODE_ADD_TEXT) {
            AnimationHelper.animate(
                context!!, colorPickerView, R.anim.slide_in_right, View.VISIBLE,
                this@PhotoEditorFragment
            )
        } else {
            AnimationHelper.animate(
                context!!, colorPickerView, R.anim.slide_out_right, View.INVISIBLE,
                this@PhotoEditorFragment
            )
        }
    }


    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.crop_btn) {
            if (selectedFilter != null) {
                ApplyFilterTask(object : TaskCallback<Bitmap> {
                    override fun onTaskDone(data: Bitmap) {
                        if (data != null) {
                            mListener!!.onCropClicked(getBitmapCache(data))
                            photoEditorView.hidePaintView()
                        }
                    }
                }, Bitmap.createBitmap(originalBitmap!!)).execute(selectedFilter)
            } else {
                mListener!!.onCropClicked(getBitmapCache(originalBitmap))
                photoEditorView.hidePaintView()
            }
        } else if (id == R.id.stickers_btn) {
            setMode(MODE_STICKER)
        } else if (id == R.id.add_text_btn) {
            setMode(MODE_ADD_TEXT)
        } else if (id == R.id.paint_btn) {
            setMode(MODE_PAINT)
        } else if (id == R.id.back_iv) {
            activity!!.onBackPressed()
        } else if (id == R.id.done_btn) {
            if (selectedFilter != null) {
                ApplyFilterTask(object : TaskCallback<Bitmap> {
                    override fun onTaskDone(data: Bitmap) {
                        if (data != null) {
                            ProcessingImage(getBitmapCache(data), Utility.getCacheFilePath(view.context),
                                object : TaskCallback<String> {
                                    override fun onTaskDone(data: String) {
                                        mListener!!.onDoneClicked(data)
                                    }
                                }).execute()
                        }
                    }
                }, Bitmap.createBitmap(mainBitmap!!)).execute(selectedFilter)
            } else {
                if (!dir!!.exists()) {
                    dir!!.mkdirs()
                }
                if (photo1!!.exists()) {
                    photo1!!.delete()
                }

                ProcessingImage(getBitmapCache(mainBitmap), photo1!!.absolutePath,
                    object : TaskCallback<String> {
                        override fun onTaskDone(data: String) {

                            /*   if (photoOrignal != null && photoOrignal!!.exists() && photoOrignal!!.absolutePath.toLowerCase().contains(
                                       "_edited"
                                   )
                               ) {
                                   photoOrignal!!.delete()
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                       val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                       val contentUri = Uri.fromFile(photoOrignal)
                                       scanIntent.data = contentUri
                                       activity!!.sendBroadcast(scanIntent)
                                   } else {
                                       activity!!.sendBroadcast(
                                           Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(photoOrignal!!.absolutePath))
                                       )
                                   }
                               }

                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                   val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                   val contentUri = Uri.fromFile(photo1)
                                   scanIntent.data = contentUri
                                   activity!!.sendBroadcast(scanIntent)
                               } else {
                                   activity!!.sendBroadcast(
                                       Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(photo1!!.absolutePath))
                                   )
                               }*/
                            mListener!!.onDoneClicked(data)
                        }
                    }).execute()
            }

        }

        if (currentMode != MODE_NONE) {
            filterLabel.alpha = 0f
            mainImageView.animate().scaleX(1f)
            photoEditorView.animate().scaleX(1f)
            mainImageView.animate().scaleY(1f)
            photoEditorView.animate().scaleY(1f)
            filterLayout.animate().translationY(filterLayoutHeight.toFloat())
            //touchView.setVisibility(View.GONE);
        } else {
            filterLabel.alpha = 1f
            //touchView.setVisibility(View.VISIBLE);
        }
    }

    private fun onAddTextMode(status: Boolean) {
        if (status) {
            addTextButton.background = Utility.tintDrawable(context!!, R.drawable.circle2, Color.parseColor("#03A9F4"))
            //photoEditorView.setTextColor(photoEditorView.getColor());
            photoEditorView.addText()

        } else {
            addTextButton.background = null
            photoEditorView.hideTextMode()

        }
    }

    private fun onPaintMode(status: Boolean) {
        if (status) {
            paintButton.background = Utility.tintDrawable(context!!, R.drawable.circle2, Color.parseColor("#03A9F4"))
            photoEditorView.showPaintView()
            //paintEditView.setVisibility(View.VISIBLE);
        } else {
            paintButton.background = null
            photoEditorView.hidePaintView()
            //photoEditorView.enableTouch(true);
            //paintEditView.setVisibility(View.GONE);
        }
    }

    private fun onStickerMode(status: Boolean) {
        if (status) {
            stickerButton.background = Utility.tintDrawable(context!!, R.drawable.circle2, Color.parseColor("#03A9F4"))
            if (activity != null && activity!!.intent != null) {
                val folderName = activity!!.intent.getStringExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME)
                photoEditorView.showStickers(folderName)
            }
        } else {
            stickerButton.background = null
            photoEditorView.hideStickers()
        }
    }

    override fun onStartViewChangeListener(view: View) {
        Log.i(ImageEditActivity::class.java.simpleName, "onStartViewChangeListener" + "" + view.id)
        toolbarLayout.visibility = View.GONE
        AnimationHelper.animate(context!!, deleteButton, R.anim.fade_in_medium, View.VISIBLE, this@PhotoEditorFragment)
    }

    override fun onStopViewChangeListener(view: View) {
        Log.i(ImageEditActivity::class.java.simpleName, "onStopViewChangeListener" + "" + view.id)
        deleteButton.visibility = View.GONE
        AnimationHelper.animate(context!!, toolbarLayout, R.anim.fade_in_medium, View.VISIBLE, this@PhotoEditorFragment)
    }

    private fun getBitmapCache(bitmap: Bitmap?): Bitmap {
        val touchMatrix = mainImageView.imageViewMatrix

        val resultBit = Bitmap.createBitmap(bitmap!!).copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBit)

        val data = FloatArray(9)
        touchMatrix.getValues(data)
        val cal = Matrix3(data)
        val inverseMatrix = cal.inverseMatrix()
        val m = Matrix()
        m.setValues(inverseMatrix.values)

        val f = FloatArray(9)
        m.getValues(f)
        val dx = f[Matrix.MTRANS_X].toInt()
        val dy = f[Matrix.MTRANS_Y].toInt()
        val scale_x = f[Matrix.MSCALE_X]
        val scale_y = f[Matrix.MSCALE_Y]
        canvas.save()
        canvas.translate(dx.toFloat(), dy.toFloat())
        canvas.scale(scale_x, scale_y)

        photoEditorView.isDrawingCacheEnabled = true
        if (photoEditorView.drawingCache != null) {
            canvas.drawBitmap(photoEditorView.drawingCache, 0f, 0f, null)
        }

        if (photoEditorView.paintBit != null) {
            canvas.drawBitmap(photoEditorView.paintBit, 0f, 0f, null)
        }

        canvas.restore()
        return resultBit
    }

    override fun onFilterSelected(imageFilter: ImageFilter, pos: Int) {
        selectedFilter = imageFilter
        ApplyFilterTask(object : TaskCallback<Bitmap> {
            override fun onTaskDone(data: Bitmap) {
                if (data != null) {
                    setImageBitmap(data)
                }
            }
        }, Bitmap.createBitmap(mainBitmap!!)).execute(imageFilter)
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

    interface OnFragmentInteractionListener {
        fun onCropClicked(bitmap: Bitmap)

        fun onDoneClicked(imagePath: String)
    }

    companion object {

        val MODE_NONE = 0
        val MODE_PAINT = 1
        val MODE_ADD_TEXT = 2
        val MODE_STICKER = 3

        fun newInstance(imagePath: String, optionsImagePath: String, currentMode: Int): PhotoEditorFragment {
            val bundle = Bundle()
            bundle.putString(ImageEditor.EXTRA_IMAGE_PATH, imagePath)
            bundle.putString("options", optionsImagePath)
            bundle.putInt("mode", currentMode)
            val photoEditorFragment = PhotoEditorFragment()
            photoEditorFragment.arguments = bundle
            return photoEditorFragment
        }
    }
}// Required empty public constructor
