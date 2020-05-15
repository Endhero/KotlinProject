package com.lcd.kotlinproject.view.adapter.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.viewholder.PictureViewHolder
import com.yuwell.androidbase.tool.DensityUtil

class PictureAdapter(var context: Context): RecyclerView.Adapter<PictureViewHolder> (){
    private var mData : MutableList<String>? = null

    override fun onCreateViewHolder(viewfroup: ViewGroup, viewType: Int) =
        PictureViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_picture, viewfroup, false)
        )

    override fun getItemCount()= mData?.size ?: 0

    override fun onBindViewHolder(pictureviewholder: PictureViewHolder, nPosition: Int) {
        Glide.with(context).load(mData!![nPosition]).into(object : ImageViewTarget<Drawable>(pictureviewholder.imageview) {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                pictureviewholder.imageview.setImageBitmap(clipRoundBitmap(transfromDrawableToBitmap(resource), 5))
                pictureviewholder.imageview.setOnClickListener {DialogUtil.showPhotoViewDialog(context, mData!![nPosition])}
            }

            override fun setResource(resource: Drawable?) {}
        })
    }

    private fun transfromDrawableToBitmap(drawable: Drawable): Bitmap {
        val width = drawable.intrinsicWidth
        val heigh = drawable.intrinsicHeight

        drawable.setBounds(0, 0, width, heigh)

        val config = if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(width, heigh, config)
        val canvas = Canvas(bitmap)

        drawable.draw(canvas)

        return bitmap
    }

    private fun clipRoundBitmap(bitmap: Bitmap, nRadius: Int): Bitmap? {
        var width = bitmap.width
        var height = bitmap.height
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float

        if (width <= height) {
            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()
            height = width
            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            val clip = (width - height) / 2.toFloat()
            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height
            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, DensityUtil.dip2px(context, nRadius.toFloat()).toFloat(), DensityUtil.dip2px(context, nRadius.toFloat()).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, src, dst, paint)

        return output
    }

    fun setData(listData: List<String>) {
        mData?.apply {
            addAll(listData)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        mData?.clear().let {notifyDataSetChanged()}
    }
}