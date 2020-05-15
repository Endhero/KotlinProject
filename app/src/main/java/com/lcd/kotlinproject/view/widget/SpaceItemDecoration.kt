package com.lcd.kotlinproject.view.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by Chen on 2019/11/13.
 */
class SpaceItemDecoration(private val verticalMargin: Int, private val horizontalMargin: Int? = 0) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = verticalMargin
        outRect.left = horizontalMargin ?: 0
        outRect.right = horizontalMargin ?: 0

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalMargin
        }
    }
}