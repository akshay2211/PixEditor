package com.fxn.pixeditor.imageeditengine.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.fxn.pixeditor.R
import kotlinx.android.synthetic.main.fragment_preview.view.*
import java.util.*

class PreviewViewPagerAdapter(private val context: Context) : PagerAdapter() {
    var list = ArrayList<String>()
        set(list) {
            this.list.clear()
            this.list.addAll(list)
        }
    private val glide: RequestManager
    private val options: RequestOptions

    init {
        glide = Glide.with(context)
        options = RequestOptions().transform(CenterCrop()).transform(FitCenter())
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(context).inflate(R.layout.fragment_preview, container, false) as ViewGroup
        val imageView = layout.image_iv
        //Log.e("string", "->------------------------------------ vp ");

        glide.load(this.list[position]).diskCacheStrategy(DiskCacheStrategy.NONE).apply(options).into(imageView)
        // imageView.setImageBitmap(BitmapFactory.decodeFile());
        container.addView(layout)
        return layout
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }


    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getCount(): Int {
        return this.list.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
//Log.e("string","check "+b);
        return view === o
    }
}

