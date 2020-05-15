package com.lcd.kotlinproject.view.adapter.base

import androidx.recyclerview.widget.RecyclerView

open abstract class BaseListAdapter<T: RecyclerView.ViewHolder, S> : RecyclerView.Adapter<T>() {
    abstract fun setData(list: MutableList<S>?)

    abstract fun clearData()
}