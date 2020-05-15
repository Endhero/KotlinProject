package com.lcd.kotlinproject.view.widget

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.lcd.kotlinproject.R
import com.yuwell.androidbase.tool.DensityUtil

class SendMessageDialog (context: Context, themeResId: Int = 0): Dialog(context, themeResId) {
    init {
        val view = View.inflate(context, R.layout.dialog_send_message, null)
        val onclicklistener = View.OnClickListener { dismiss() }
        val imageview = view.findViewById<ImageView>(R.id.imageview_close)
        imageview.setOnClickListener(onclicklistener)

        val button = view.findViewById<Button>(R.id.button_i_see)
        button.setOnClickListener(onclicklistener)

        setContentView(view)
    }

    override fun show() {
        super.show()

        val displaymetrics = DisplayMetrics()

        window?.windowManager?.defaultDisplay?.getRealMetrics(displaymetrics)

        val layoutparams = window?.attributes
        layoutparams?.width = displaymetrics.widthPixels - DensityUtil.dip2px(context, 18f) * 2

        window?.attributes = layoutparams
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}