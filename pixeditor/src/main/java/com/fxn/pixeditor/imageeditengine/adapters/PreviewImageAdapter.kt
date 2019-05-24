package com.fxn.pixeditor.imageeditengine.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.interfaces.OnSelectionStringListener
import com.fxn.pixeditor.imageeditengine.utils.Utility
import java.io.File
import java.util.*

class PreviewImageAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = ArrayList<String>()
    private var onSelectionListener: OnSelectionStringListener? = null
    private val glide: RequestManager
    private val options: RequestOptions
    private val size: Float
    private val margin = 2
    private val padding: Int

    init {
        this@PreviewImageAdapter.list.clear()
        size = Utility.convertDpToPixel(60f, context) - 2
        padding = (size / 3.5).toInt()
        glide = Glide.with(context)
        options = RequestOptions().override(256).transform(CenterCrop()).transform(FitCenter())
    }

    fun addImage(list: ArrayList<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addOnSelectionListener(onSelectionListener: OnSelectionStringListener) {
        this.onSelectionListener = onSelectionListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // if (viewType == 0) {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.edit_thub_image, parent, false)
        return Holder(v)
        // }
    }

    override fun getItemViewType(position: Int): Int {
        return /*if (position == 0) 1 else*/ 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val imageHolder = holder as Holder
            val f = File(list[position])
            val layoutParams = FrameLayout.LayoutParams(size.toInt(), size.toInt())
            layoutParams.setMargins(margin, margin, margin, margin)
            imageHolder.itemView.layoutParams = layoutParams
            glide.load(f).diskCacheStrategy(DiskCacheStrategy.NONE).apply(options)
                .into(imageHolder.itemView as ImageView)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removevalue(num: String) {
        list.remove(num)
        notifyDataSetChanged()
    }


    inner class Holder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val id = this.layoutPosition
            onSelectionListener!!.onClick(list[id], view, id)
        }
    }
}
