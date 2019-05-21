package com.fxn.pixeditor.imageeditengine.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.fxn.pixeditor.R
import com.fxn.pixeditor.imageeditengine.model.BitmapObject
import kotlinx.android.synthetic.main.fragment_preview.view.*
import java.io.File
import java.util.*

class PreviewViewPagerAdapter(private val context: Context) : PagerAdapter() {
    var list = ArrayList<BitmapObject>()
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
        val mainImageView = layout.image_iv
        //Log.e("string", "->------------------------------------ vp ");
        var obj = this.list[position]
        obj.photoOrignal = File(obj.path)
        //mainImageView.post(new Runnable() {
        //  @Override public void run() {
        //    mainBitmap = Utility.decodeBitmap(imagePath,mainImageView.getWidth(),mainImageView.getHeight());
        //
        //  }
        //});

        // Log.e("imagePath","->"+imagePath);
        if (obj.orignalBitmap == null) {
            Glide.with(context)
                .asBitmap()
                .load(obj.path)
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
                            obj.orignalBitmap = Bitmap.createScaledBitmap(resource, ivWidth, newHeight, true)
                            obj.mainBitmap = obj.orignalBitmap
                        } catch (e: Exception) {
                            obj.mainBitmap = resource
                        }

                        mainImageView.setImageBitmap(obj.mainBitmap)

                        /*  mainImageView.post {
                              if (obj.mainBitmap != null) {
                                  var height = majorContainer!!.height
                                  val layouparams = topPaddingView.layoutParams
                                  layouparams.height = height / 2 - bitmap.height / 2
                                  topPaddingView.layoutParams = layouparams
                                  val layouparams1 = bottomPaddingView.layoutParams
                                  layouparams1.height = height / 2 - bitmap.height / 2
                                  bottomPaddingView.layoutParams = layouparams1
                                  photoEditorView.setBounds(mainImageView.bitmapRect!!)
                              }
                          }*/
                        /*GetFiltersTask(object : TaskCallback<ArrayList<ImageFilter>> {
                            override fun onTaskDone(data: ArrayList<ImageFilter>) {
                                val filterImageAdapter = filterRecylerview.adapter as FilterImageAdapter
                                if (filterImageAdapter != null) {
                                    filterImageAdapter!!.setData(data)
                                    filterImageAdapter!!.notifyDataSetChanged()
                                }
                            }
                        }, obj.mainBitmap!!).execute()*/
                    }
                })
        } else {
            mainImageView.setImageBitmap(obj.mainBitmap)
        }
        //glide.load(this.list[position]).diskCacheStrategy(DiskCacheStrategy.NONE).apply(options).into(imageView)
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

