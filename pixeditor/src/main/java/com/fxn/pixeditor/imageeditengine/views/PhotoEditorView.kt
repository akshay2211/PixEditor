package com.fxn.pixeditor.imageeditengine.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.Dimension
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.utils.KeyboardHeightProvider
import com.fxn.pixeditor.imageeditengine.utils.MultiTouchListener
import com.fxn.pixeditor.imageeditengine.utils.Utility
import java.io.IOException
import java.util.*

class PhotoEditorView : FrameLayout, ViewTouchListener, KeyboardHeightProvider.KeyboardHeightObserver {


    internal lateinit var container: RelativeLayout
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var customPaintView: CustomPaintView
    private var folderName: String? = null
    private var imageView: ImageView? = null
    private var deleteView: ImageView? = null
    private var viewTouchListener: ViewTouchListener? = null
    private var selectedView: View? = null
    private var selectViewIndex: Int = 0
    lateinit var inputTextET: EditText
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var initialY: Float = 0.toFloat()
    private var containerView: View? = null
    private var bottomImageView: ImageView? = null
    private var topImageView: ImageView? = null

    var color: Int
        get() = customPaintView.color
        set(selectedColor) {
            customPaintView.color = selectedColor
        }

    val paintBit: Bitmap?
        get() = customPaintView.paintBit

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val view = View.inflate(getContext(), R.layout.photo_editor_view, null)
        container = view.findViewById(R.id.container)
        containerView = view.findViewById(R.id.container_view)
        recyclerView = view.findViewById(R.id.recyclerview)
        inputTextET = view.findViewById(R.id.add_text_et)
        // Log.e("has sft ","keyboard "+isNavigationBarAvailable());
        /*  if (ImageEditActivity.hasSoftKeys) {
      RelativeLayout.LayoutParams layoutParams =
          (RelativeLayout.LayoutParams) inputTextET.getLayoutParams();
      ((RelativeLayout.LayoutParams) layoutParams).bottomMargin = inputTextET.getHeight() * 2;
      inputTextET.setLayoutParams(layoutParams);
    }*/
        customPaintView = view.findViewById(R.id.paint_view)
        inputTextET.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    if (selectedView != null) {
                        (selectedView as AutofitTextView).text = inputTextET.text
                        Utility.hideSoftKeyboard(getContext() as Activity)
                    } else {
                        createText(inputTextET.text.toString())
                        Utility.hideSoftKeyboard(getContext() as Activity)
                    }
                    inputTextET.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            false
        }
        keyboardHeightProvider = KeyboardHeightProvider(getContext() as Activity)
        keyboardHeightProvider!!.setKeyboardHeightObserver(this)

        val gridLayoutManager = GridLayoutManager(getContext(), 4)
        recyclerView.layoutManager = gridLayoutManager

        val stickerAdapter = StickerListAdapter(ArrayList())
        recyclerView.adapter = stickerAdapter

        view.post { keyboardHeightProvider!!.start() }

        /*  inputTextET.post(new Runnable() {
      @Override
      public void run() {

        if (initialY==0.0){
          initialY = inputTextET.getY();
        }
      }
    });*/
        inputTextET.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Layout has happened here.
                    initialY = inputTextET.y
                    Log.e("initialY", "---------------->$initialY")
                    // Don't forget to remove your listener when you are done with it.
                    inputTextET.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            })
        addView(view)
    }

    fun showPaintView() {
        bottomImageView!!.visibility = View.VISIBLE
        topImageView!!.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        inputTextET.visibility = View.GONE
        Utility.hideSoftKeyboard(context as Activity)
        customPaintView.bringToFront()
        customPaintView.isEnabled = true
    }

    fun setBounds(bitmapRect: RectF) {
        customPaintView.setBounds(bitmapRect)
    }

    fun hidePaintView() {
        containerView!!.bringToFront()
        customPaintView.isEnabled = false
    }

    //text mode methods
    fun setImageView(
        topImageView: ImageView, bottomImageView: ImageView, imageView: ImageView, deleteButton: ImageView,
        viewTouchListener: ViewTouchListener
    ) {
        this.imageView = imageView
        this.topImageView = topImageView
        this.bottomImageView = bottomImageView
        this.deleteView = deleteButton
        this.viewTouchListener = viewTouchListener
    }

    fun setTextColor(selectedColor: Int) {
        try {
            var autofitTextView: AutofitTextView? = null
            if (selectedView != null) {
                autofitTextView = selectedView as AutofitTextView?
                autofitTextView!!.setTextColor(selectedColor)
            } else {
                val view = getViewChildAt(selectViewIndex)
                if (view != null && view is AutofitTextView) {
                    autofitTextView = view
                    autofitTextView.setTextColor(selectedColor)
                }
            }
            inputTextET.setTextColor(selectedColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    fun addText() {
        inputTextET.visibility = View.VISIBLE
        inputTextET.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Layout has happened here.
                    initialY = inputTextET.y
                    Log.e("initialY", "---------------->$initialY")
                    // Don't forget to remove your listener when you are done with it.
                    inputTextET.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            })
        bottomImageView!!.visibility = View.VISIBLE
        topImageView!!.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        containerView!!.bringToFront()
        //containerView.bringToFront();
        inputTextET.setText("")
        Handler().postDelayed({
            inputTextET.bringToFront()

            Utility.showSoftKeyboard(context as Activity, inputTextET)
        }, 100)
    }

    fun hideTextMode() {
        Utility.hideSoftKeyboard(context as Activity)
        inputTextET.visibility = View.INVISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: View.OnTouchListener) {
        super.setOnTouchListener(l)
        containerView!!.setOnTouchListener(l)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createText(text: String) {
        val autofitTextView = LayoutInflater.from(context).inflate(R.layout.text_editor, null) as AutofitTextView
        autofitTextView.id = container.childCount
        autofitTextView.text = text
        autofitTextView.setTextColor(inputTextET.currentTextColor)
        autofitTextView.setMaxTextSize(Dimension.SP, 50f)
        val multiTouchListener =
            MultiTouchListener(topImageView!!, bottomImageView!!, deleteView, container, this.imageView!!, true, this)
        multiTouchListener.setOnMultiTouchListener(object : MultiTouchListener.OnMultiTouchListener {

            override fun onRemoveViewListener(removedView: View) {
                container.removeView(removedView)
                inputTextET.text = null
                inputTextET.visibility = View.INVISIBLE
                selectedView = null
            }
        })
        multiTouchListener.setOnGestureControl(object : MultiTouchListener.OnGestureControl {
            override fun onClick(currentView: View?) {
                if (currentView != null) {
                    selectedView = currentView
                    selectViewIndex = currentView.id
                    inputTextET.visibility = View.VISIBLE
                    inputTextET.setText((currentView as AutofitTextView).text)
                    inputTextET.setSelection(inputTextET.text.length)
                    Log.i("ViewNum", ":" + selectViewIndex + " " + currentView.text)
                }

                Utility.showSoftKeyboard(context as Activity, inputTextET)
            }

            override fun onLongClick() {

            }
        })
        autofitTextView.setOnTouchListener(multiTouchListener)

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        container.addView(autofitTextView, params)

        selectViewIndex = container.getChildAt(container.childCount - 1).id
        selectedView = null
    }

    override fun onStartViewChangeListener(view: View) {
        Utility.hideSoftKeyboard(context as Activity)
        if (viewTouchListener != null) {
            viewTouchListener!!.onStartViewChangeListener(view)
        }
    }

    override fun onStopViewChangeListener(view: View) {
        if (viewTouchListener != null) {
            viewTouchListener!!.onStopViewChangeListener(view)
        }
    }

    override fun onStartViewFullChangeListener(view: View) {
        Utility.hideSoftKeyboard(context as Activity)
        if (viewTouchListener != null) {
            viewTouchListener!!.onStartViewFullChangeListener(view)
        }
    }

    override fun onStopViewFullChangeListener(view: View) {
        if (viewTouchListener != null) {
            viewTouchListener!!.onStopViewFullChangeListener(view)
        }
    }


    private fun getViewChildAt(index: Int): View? {
        return if (index > container.childCount - 1) {
            null
        } else container.getChildAt(index)
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        Handler().postDelayed({
            if (initialY == 0f && inputTextET.y != 0f) {
            }
            Log.e("check", "initialY ->$initialY  height-> $height")

            if (height == 0) {
                inputTextET.y = initialY
                inputTextET.requestLayout()
            } else {
                val newPosition = initialY - height
                inputTextET.y = newPosition
                inputTextET.requestLayout()
            }
        }, 200)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        keyboardHeightProvider!!.close()
    }

    fun showStickers(stickersFolder: String) {

        bottomImageView!!.visibility = View.GONE
        topImageView!!.visibility = View.GONE
        containerView!!.bringToFront()
        recyclerView.visibility = View.VISIBLE
        inputTextET.visibility = View.GONE
        Utility.hideSoftKeyboard(context as Activity)
        this.folderName = stickersFolder
        val stickerListAdapter = recyclerView.adapter as StickerListAdapter
        stickerListAdapter.setData(getStickersList(stickersFolder))
    }

    fun hideStickers() {
        recyclerView.visibility = View.GONE
    }

    private fun getStickersList(folderName: String): MutableList<String>? {
        val assetManager = context.assets
        try {
            val lists = assetManager.list(folderName)
            val mylist = Arrays.asList(*lists)
            try {
                Collections.sort(mylist, object : Comparator<String> {
                    override fun compare(o1: String, o2: String): Int {
                        return extractInt(o1) - extractInt(o2)
                    }

                    fun extractInt(s: String): Int {
                        val num = s.replace("\\D".toRegex(), "")
                        // return 0 if no digits found
                        return if (num.isEmpty()) 0 else Integer.parseInt(num)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mylist
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onItemClick(bitmap: Bitmap?) {
        recyclerView.visibility = View.GONE
        bottomImageView!!.visibility = View.VISIBLE
        topImageView!!.visibility = View.VISIBLE
        val stickerImageView = LayoutInflater.from(context).inflate(R.layout.sticker_view, null) as ImageView
        stickerImageView.setImageBitmap(bitmap)
        stickerImageView.id = container.childCount
        val multiTouchListener =
            MultiTouchListener(topImageView!!, bottomImageView!!, deleteView, container, this.imageView!!, true, this)
        multiTouchListener.setOnMultiTouchListener(object : MultiTouchListener.OnMultiTouchListener {

            override fun onRemoveViewListener(removedView: View) {
                container.removeView(removedView)
                selectedView = null
            }
        })
        multiTouchListener.setOnGestureControl(object : MultiTouchListener.OnGestureControl {
            override fun onClick(currentView: View?) {
                if (currentView != null) {
                    selectedView = currentView
                    selectViewIndex = currentView.id
                }
            }

            override fun onLongClick() {

            }
        })
        stickerImageView.setOnTouchListener(multiTouchListener)

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        container.addView(stickerImageView, params)
    }

    fun reset() {
        container.removeAllViews()
        customPaintView.reset()
        invalidate()
    }

    fun crop(cropRect: Rect) {
        container.removeAllViews()
        customPaintView.reset()
        invalidate()
    }

    inner class StickerListAdapter(list: ArrayList<String>) : RecyclerView.Adapter<StickerListAdapter.ViewHolder>() {
        override fun getItemCount(): Int {
            return stickers!!.size
        }

        private var stickers: MutableList<String>? = null

        init {
            stickers = list
        }

        fun setData(stickersList: MutableList<String>?) {
            this.stickers = stickersList
            notifyDataSetChanged()
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

        fun add(position: Int, item: String) {
            stickers!!.add(position, item)
            notifyItemInserted(position)
        }

        fun remove(position: Int) {
            stickers!!.removeAt(position)
            notifyItemRemoved(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerListAdapter.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val v = inflater.inflate(R.layout.sticker_view, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val path = stickers!![position]
            holder.itemView.setOnClickListener(OnClickListener { onItemClick(getImageFromAssetsFile(path)) })
            (holder.itemView as ImageView).setImageBitmap(getImageFromAssetsFile(path))
        }

        private fun getImageFromAssetsFile(fileName: String): Bitmap? {
            var image: Bitmap? = null
            val am = resources.assets
            try {
                val `is` = am.open("$folderName/$fileName")
                image = BitmapFactory.decodeStream(`is`)
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return image
        }
    }
}