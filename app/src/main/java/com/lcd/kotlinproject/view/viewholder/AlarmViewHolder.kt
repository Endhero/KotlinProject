package com.lcd.kotlinproject.view.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lcd.kotlinproject.R

class AlarmViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var imageviewType: ImageView = view.findViewById(R.id.imageview_type)
    var textViewName: TextView = view.findViewById(R.id.textview_name)
    var textViewWarning: TextView = view.findViewById(R.id.textview_warning)
    var textviewLevel: TextView = view.findViewById(R.id.textview_level)
    var textviewWarningDate: TextView = view.findViewById(R.id.textview_warning_date_val)
    var textviewDealDate: TextView = view.findViewById(R.id.textview_deal_date_val)
    var divider: View = view.findViewById(R.id.divider)
}