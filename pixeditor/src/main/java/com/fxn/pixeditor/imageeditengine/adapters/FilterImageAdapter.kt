package com.fxn.pixeditor.imageeditengine.adapters

import android.animation.LayoutTransition
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.model.ImageFilter
import com.fxn.pixeditor.imageeditengine.utils.Utility
import java.util.*

class FilterImageAdapter(list: ArrayList<ImageFilter>, private val mListener: FilterImageAdapterListener) :
    RecyclerView.Adapter<FilterImageAdapter.ViewHolder>() {


    private var imageFilters: MutableList<ImageFilter>? = null
    public var lastCheckedPostion = 0

    override fun getItemCount(): Int {
        return imageFilters!!.size
    }

    interface FilterImageAdapterListener {
        fun onFilterSelected(imageFilter: ImageFilter, pos: Int)
    }

    init {
        imageFilters = list
    }

    fun setData(stickersList: MutableList<ImageFilter>) {
        this.imageFilters = stickersList
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        internal var filterIV: ImageView
        internal var checkbox: ImageView
        internal var filterTv: TextView
        internal var container: FrameLayout

        init {
            filterIV = v.findViewById(R.id.filter_iv)
            filterTv = v.findViewById(R.id.filter_name)
            checkbox = v.findViewById(R.id.check_box)
            container = v.findViewById(R.id.container)
            container.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }
    }

    fun add(position: Int, item: ImageFilter) {
        imageFilters!!.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        imageFilters!!.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_filter_layout, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFilter = imageFilters!![position]
        Log.i("filter", imageFilter.filterName)
        if (imageFilter.filterImage != null) {
            holder.filterIV.setImageBitmap(imageFilter.filterImage)
        }

        var layoutParams: FrameLayout.LayoutParams? = null
        if (position == lastCheckedPostion) {
            holder.checkbox.visibility = View.VISIBLE
            layoutParams = FrameLayout.LayoutParams(
                Utility.dpToPx(holder.checkbox.context, 70),
                Utility.dpToPx(holder.checkbox.context, 110)
            )

        } else {
            holder.checkbox.visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                Utility.dpToPx(holder.checkbox.context, 64),
                Utility.dpToPx(holder.checkbox.context, 100)
            )
        }
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        holder.filterIV.layoutParams = layoutParams
        holder.filterTv.setText(imageFilter.filterName)
        holder.itemView.setOnClickListener(View.OnClickListener {
            mListener.onFilterSelected(imageFilter, position)
            val lastPosition = lastCheckedPostion
            holder.checkbox.visibility = View.VISIBLE

            val layoutParams = FrameLayout.LayoutParams(
                Utility.dpToPx(holder.checkbox.context, 70),
                Utility.dpToPx(holder.checkbox.context, 110)
            )
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            holder.filterIV.layoutParams = layoutParams
            lastCheckedPostion = holder.getAdapterPosition()
            notifyItemChanged(lastPosition)
        })
    }

    fun scaleView(v: View, startScale: Float, endScale: Float, duration: Int) {
        val anim = ScaleAnimation(
            startScale, endScale, // Start and end values for the X axis scaling
            startScale, endScale, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 1f
        ) // Pivot point of Y scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = duration.toLong()
        v.startAnimation(anim)
    }
}