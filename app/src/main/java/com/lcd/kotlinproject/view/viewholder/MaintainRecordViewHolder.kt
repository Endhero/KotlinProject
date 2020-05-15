package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R

/**
 * Created by liuchangda 2020/ 04/ 14
 */
class MaintainRecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var viewDividerTop: View = view.findViewById(R.id.view_line_top)
    var textviewDate: TextView = view.findViewById(R.id.textview_date)
    var textviewType: TextView = view.findViewById(R.id.textview_type)
    var textviewMaintainRunningTime: TextView = view.findViewById(R.id.textview_maintain_running_time)
    var textviewMaintainTime: TextView = view.findViewById(R.id.textview_maintain_time)
    var textviewMaintainContent: TextView = view.findViewById(R.id.textview_maintain_content)
}