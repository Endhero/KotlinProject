package com.lcd.kotlinproject.view.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.lcd.kotlinproject.R

class PhotoViewDialog @JvmOverloads constructor(context: Context, themeResId: Int = 0): Dialog(context, themeResId) {
    private val mPhotoView: PhotoView
    private val mTextViewBack: TextView

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view =
            View.inflate(context, R.layout.dialog_photoview, null)

        mPhotoView = view.findViewById(R.id.photoview)
        mTextViewBack = view.findViewById<TextView>(R.id.textview_back)
        mTextViewBack.setOnClickListener{ dismiss() }

        mPhotoView.maximumScale = 3f

        setContentView(view)
    }

    fun setImagePath(strPath: String?) {
        if (!strPath.isNullOrBlank()){
            Glide.with(context).load(strPath).into(mPhotoView)
        }
    }

    fun setImageResource(nImageResourc: Int) {
        Glide.with(context).load(nImageResourc).into(mPhotoView)
    }

    override fun show() {
        super.show()

        window?.setBackgroundDrawable(null)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun setMaximumScale(fMaximumScale: Float) {
        if (fMaximumScale > mPhotoView.maximumScale) {
            mPhotoView.maximumScale = fMaximumScale
        }
    }

    fun setMinimumScale(fMinimumScale: Float) {
        if (fMinimumScale < mPhotoView.maximumScale) {
            mPhotoView.minimumScale = fMinimumScale
        }
    }
}