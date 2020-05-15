package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R

/**
 * Created by liuchangda 2020/ 04/ 14
 */

class MaintainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageviewType: ImageView = view.findViewById(R.id.imageview_type)
    var textviewGroup: TextView = view.findViewById(R.id.textview_group)
    var textviewName: TextView = view.findViewById(R.id.textview_name)
    var textviewMaintainTimeRemain: TextView = view.findViewById(R.id.textview_maintain_time_remain_value)
    var textviewTime: TextView = view.findViewById(R.id.textview_time)
    var textviewType: TextView = view.findViewById(R.id.textview_type)
    var textviewContent: TextView = view.findViewById(R.id.textview_content)
    var textviewMore: TextView = view.findViewById(R.id.textview_more)
}