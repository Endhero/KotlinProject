package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.lcd.kotlinproject.R

/**
 * Created by Chen on 2020/3/17.
 */
class ConditionsSelect @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    @BindView(R.id.text_left)
    lateinit var textLeft: TextView
    @BindView(R.id.text_right)
    lateinit var textRight: TextView
    
    private var onItemClickListener: OnItemClickListener? = null

    @OnClick(R.id.layout_left, R.id.layout_right)
    fun onClick(view: View) {
        when (view.id) {
            R.id.layout_left ->  onItemClickListener?.onLeftClick()
            R.id.layout_right -> onItemClickListener?.onRightClick()
        }
    }

    fun setLeftText(s: CharSequence?) = run { textLeft.text = s }

    fun setRightText(s: CharSequence?) = run { textRight.text = s }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?)  = run {  this.onItemClickListener = onItemClickListener }

    interface OnItemClickListener {
        fun onLeftClick()
        fun onRightClick()
    }

    init {
        ButterKnife.bind(this, LayoutInflater.from(context).inflate(R.layout.widget_conditions_select, this))
    }
}