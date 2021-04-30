package com.thewyp.android.criminalintent.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

object PictureUtil {

    fun getScaleBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
        //Read in the dimensions of the image on disk
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val srcWidth = options.outWidth.toFloat()
        val srcHeigh = options.outHeight.toFloat()

        //Figure out how much to scale down by
        var inSampleSize = 1
        if (srcHeigh > destHeight || srcWidth > destWidth) {
            val heightScale = srcHeigh / destHeight
            val widthScale = srcWidth / destWidth

            val sampleScale = if (heightScale > widthScale) {
                heightScale
            } else {
                widthScale
            }
            inSampleSize = sampleScale.roundToInt()
        }

        options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options)
    }
}