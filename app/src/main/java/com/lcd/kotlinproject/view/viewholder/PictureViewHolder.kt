package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R

/**
 * Created by liuchangda 2019/ 12/ 12
 */
class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageview: ImageView = view.findViewById(R.id.imageview)
}