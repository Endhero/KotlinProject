package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R

/**
 * Created by liuchangda 2020/ 04/ 15
 */
class MaintainJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageviewType: ImageView = view.findViewById(R.id.imageview_type)
    var textviewGroup: TextView = view.findViewById(R.id.textview_group)
    var textviewName: TextView = view.findViewById(R.id.textview_name)
    var textviewContent: TextView = view.findViewById(R.id.textview_job_list_content)
    var textviewDealTime: TextView = view.findViewById(R.id.textview_deal_time)
    var divider: View = view.findViewById(R.id.divider)
}