package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R
import com.shehuan.niv.NiceImageView

/**
 * Created by liuchangda 2020/ 03/ 12
 */
class CameraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var textviewName: TextView = view.findViewById(R.id.textview_name)
    var textviewState: TextView = view.findViewById(R.id.textview_state)
    var textviewErrorTip: TextView = view.findViewById(R.id.textview_error_tip)
    var imageview: NiceImageView = view.findViewById(R.id.imageview_video)

}