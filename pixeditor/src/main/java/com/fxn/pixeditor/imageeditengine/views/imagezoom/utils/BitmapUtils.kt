/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fxn.pixeditor.imageeditengine.views.imagezoom.utils

import android.app.Activity
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory.Options
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import android.view.View

import java.io.*

/**
 * BitmapUtils
 *
 * @author panyi
 */
class BitmapUtils {

    class BitmapSize(var width: Int, var height: Int)

    fun printscreen_share(v: View, context: Activity) {
        val view1 = context.window.decorView
        val display = context.windowManager.defaultDisplay
        view1.layout(0, 0, display.width, display.height)
        view1.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view1.drawingCache)
    }

    companion object {
        /**
         * Used to tag logs
         */
        private val TAG = "BitmapUtils"

        val MAX_SZIE = (1024 * 512).toLong()// 500KB

        //    public static Bitmap loadImageByPath(final String imagePath, int reqWidth,
        //                                         int reqHeight) {
        //        File file = new File(imagePath);
        //        if (file.length() < MAX_SZIE) {
        //            return getSampledBitmap(imagePath, reqWidth, reqHeight);
        //        } else {// 压缩图片
        //            return getImageCompress(imagePath);
        //        }
        //    }

        fun getOrientation(imagePath: String): Int {
            var rotate = 0
            try {
                val imageFile = File(imagePath)
                val exif = ExifInterface(imageFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return rotate
        }

        fun getBitmapSize(filePath: String): BitmapSize {
            val options = Options()
            options.inJustDecodeBounds = true

            BitmapFactory.decodeFile(filePath, options)

            return BitmapSize(options.outWidth, options.outHeight)
        }

        fun getScaledSize(
            originalWidth: Int,
            originalHeight: Int, numPixels: Int
        ): BitmapSize {
            val ratio = originalWidth.toFloat() / originalHeight

            val scaledHeight = Math.sqrt((numPixels.toFloat() / ratio).toDouble()).toInt()
            val scaledWidth = (ratio * Math.sqrt((numPixels.toFloat() / ratio).toDouble())).toInt()

            return BitmapSize(scaledWidth, scaledHeight)
        }

        fun bitmapTobytes(bitmap: Bitmap): ByteArray {
            val a = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 30, a)
            return a.toByteArray()
        }

        fun bitmapTobytesNoCompress(bitmap: Bitmap): ByteArray {
            val a = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 100, a)
            return a.toByteArray()
        }

        fun genRotateBitmap(data: ByteArray): Bitmap {
            var bMap: Bitmap? = BitmapFactory.decodeByteArray(data, 0, data.size)
            // 自定义相机拍照需要旋转90预览支持竖屏
            val matrix = Matrix()// 矩阵
            matrix.reset()// 设置为单位矩阵
            matrix.postRotate(90f)// 旋转90度
            val bMapRotate = Bitmap.createBitmap(
                bMap!!, 0, 0, bMap.width,
                bMap.height, matrix, true
            )
            bMap.recycle()
            bMap = null
            System.gc()
            return bMapRotate
        }

        fun byteToBitmap(data: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }

        /**
         * 将view转为bitmap
         *
         * @param view
         * @return
         */
        fun getBitmapFromView(view: View): Bitmap {
            val returnedBitmap = Bitmap.createBitmap(
                view.width,
                view.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            val bgDrawable = view.background
            if (bgDrawable != null)
                bgDrawable.draw(canvas)
            else
                canvas.drawColor(Color.WHITE)
            view.draw(canvas)
            return returnedBitmap
        }

        // 按大小缩放
        fun getImageCompress(srcPath: String): Bitmap? {
            val newOpts = Options()
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)// 此时返回bm为空

            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            val hh = 800f// 这里设置高度为800f
            val ww = 480f// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            newOpts.inSampleSize = be// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
            return compressImage(bitmap)// 压缩好比例大小后再进行质量压缩
        }

        // 图片按比例大小压缩
        fun compress(image: Bitmap): Bitmap? {

            val baos = ByteArrayOutputStream()
            image.compress(CompressFormat.JPEG, 100, baos)
            if (baos.toByteArray().size / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                baos.reset()// 重置baos即清空baos
                image.compress(CompressFormat.JPEG, 50, baos)// 这里压缩50%，把压缩后的数据存放到baos中
            }
            var isBm = ByteArrayInputStream(baos.toByteArray())
            val newOpts = Options()
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            val hh = 800f// 这里设置高度为800f
            val ww = 480f// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            newOpts.inSampleSize = be// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = ByteArrayInputStream(baos.toByteArray())
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
            return compressImage(bitmap!!)// 压缩好比例大小后再进行质量压缩
        }

        // 图片质量压缩
        private fun compressImage(image: Bitmap): Bitmap? {
            val baos = ByteArrayOutputStream()
            image.compress(CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100

            while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset()// 重置baos即清空baos
                image.compress(CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10// 每次都减少10
                //			System.out.println("options--->" + options + "    "
                //					+ (baos.toByteArray().length / 1024));
            }
            val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null)
        }

        // 图片转为文件
        fun saveBitmap2file(bmp: Bitmap, filepath: String): Boolean {
            val format = CompressFormat.PNG
            val quality = 100
            var stream: OutputStream? = null
            try {
                // 判断SDcard状态
                if (Environment.MEDIA_MOUNTED != Environment
                        .getExternalStorageState()
                ) {
                    // 错误提示
                    return false
                }

                // 检查SDcard空间
                val SDCardRoot = Environment.getExternalStorageDirectory()
                if (SDCardRoot.freeSpace < 10000) {
                    // 弹出对话框提示用户空间不够
                    Log.e("Utils", "存储空间不够")
                    return false
                }

                // 在SDcard创建文件夹及文件
                val bitmapFile = File(SDCardRoot.path + filepath)
                bitmapFile.parentFile.mkdirs()// 创建文件夹
                stream = FileOutputStream(SDCardRoot.path + filepath)// "/sdcard/"
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return bmp.compress(format, quality, stream)
        }

        /**
         * 截屏
         *
         * @param activity
         * @return
         */
        fun getScreenViewBitmap(activity: Activity): Bitmap {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            return view.drawingCache
        }

        /**
         * 一个 View的图像
         *
         * @param view
         * @return
         */
        fun getViewBitmap(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            return view.drawingCache
        }

        /**
         * Resize a bitmap object to fit the passed width and height
         *
         * @param input
         * The bitmap to be resized
         * @param destWidth
         * Desired maximum width of the result bitmap
         * @param destHeight
         * Desired maximum height of the result bitmap
         * @return A new resized bitmap
         * @throws OutOfMemoryError
         * if the operation exceeds the available vm memory
         */
        @Throws(OutOfMemoryError::class)
        @JvmOverloads
        fun resizeBitmap(input: Bitmap, destWidth: Int, destHeight: Int, rotation: Int = 0): Bitmap {

            var dstWidth = destWidth
            var dstHeight = destHeight
            val srcWidth = input.width
            val srcHeight = input.height

            if (rotation == 90 || rotation == 270) {
                dstWidth = destHeight
                dstHeight = destWidth
            }

            var needsResize = false
            val p: Float
            if (srcWidth > dstWidth || srcHeight > dstHeight) {
                needsResize = true
                if (srcWidth > srcHeight && srcWidth > dstWidth) {
                    p = dstWidth.toFloat() / srcWidth.toFloat()
                    dstHeight = (srcHeight * p).toInt()
                } else {
                    p = dstHeight.toFloat() / srcHeight.toFloat()
                    dstWidth = (srcWidth * p).toInt()
                }
            } else {
                dstWidth = srcWidth
                dstHeight = srcHeight
            }

            if (needsResize || rotation != 0) {
                val output: Bitmap

                if (rotation == 0) {
                    output = Bitmap.createScaledBitmap(input, dstWidth, dstHeight, true)
                } else {
                    val matrix = Matrix()
                    matrix.postScale(dstWidth.toFloat() / srcWidth, dstHeight.toFloat() / srcHeight)
                    matrix.postRotate(rotation.toFloat())
                    output = Bitmap.createBitmap(input, 0, 0, srcWidth, srcHeight, matrix, true)
                }
                return output
            } else
                return input
        }

        fun getSampledBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
            val options = Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            val inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inSampleSize = inSampleSize
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options)
        }


        fun calculateInSampleSize(options: Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        /**
         * 保存Bitmap图片到指定文件
         *
         * @param bm
         */
        fun saveBitmap(bm: Bitmap, filePath: String): Boolean {
            val f = File(filePath)
            if (f.exists()) {
                f.delete()
            }
            try {
                val out = FileOutputStream(f)
                bm.compress(CompressFormat.PNG, 90, out)
                out.flush()
                out.close()
                return true
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return false
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

            // System.out.println("保存文件--->" + f.getAbsolutePath());
        }
    }

}
/**
 * Resize a bitmap
 *
 * @param input
 * @param destWidth
 * @param destHeight
 * @return
 * @throws OutOfMemoryError
 */
