package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.RecyclerView
import com.yuwell.androidbase.tool.DensityUtil

class GridRecycleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): RecyclerView(context, attrs, defStyleAttr) {
    private var mVerticalSpace = 3
    private var mHorizontal = 3
    private var mColumn = 3
    private val mGridLayoutManager: GridLayoutManager = GridLayoutManager(context, mColumn)
    private var mAdapter: RecyclerView.Adapter<*>? = null

    init {
        layoutManager = mGridLayoutManager

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var bNeedLoad = true

            override fun onGlobalLayout() {
                if (bNeedLoad) {
                    super@GridRecycleView.setAdapter(mAdapter)
                    bNeedLoad = false
                }
            }
        })
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val nPaddingTop = paddingTop
        val nPaddingLeft = paddingLeft
        val nPaddingRight = paddingRight
        val nHorizontalSpace = DensityUtil.dip2px(context, mHorizontal.toFloat())
        val nVerticalSpace = DensityUtil.dip2px(context, mVerticalSpace.toFloat())
        val nWidth = (width - DensityUtil.dip2px(context, mHorizontal.toFloat()) * (mColumn - 1) - nPaddingLeft - nPaddingRight) / mColumn

        for (i in 0 until childCount) {
            val nRow = i / mColumn
            val nColumn = i % mColumn
            val view = getChildAt(i)
            view.top = nPaddingTop + nRow * (nWidth + nVerticalSpace)
            view.bottom = view.top + nWidth
            view.left = nPaddingLeft + nColumn * (nWidth + nHorizontalSpace)
            view.right = view.left + nWidth
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        adapter?.let {
            if (width != 0) {
                val nVerticalSpace = DensityUtil.dip2px(context, mVerticalSpace.toFloat())
                val nHeight = (width - DensityUtil.dip2px(context, mHorizontal.toFloat()) * (mColumn - 1)) / mColumn
                val nRowCount = adapter!!.itemCount / mColumn + if (adapter!!.itemCount % mColumn == 0) 0 else 1

                setMeasuredDimension(widthSpec, MeasureSpec.makeMeasureSpec(nRowCount * nHeight + nVerticalSpace * (nRowCount - 1) + paddingTop + paddingBottom, MeasureSpec.EXACTLY))

                return
            }
        }

        super.onMeasure(widthSpec, heightSpec)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams) {
        params.width = (width - DensityUtil.dip2px(
            context,
            mHorizontal.toFloat()
        ) * (mColumn - 1)) / mColumn
        params.height = params.width
        super.addView(child, index, params)
    }

    fun setVerticalSpace(nVerticalSpace: Int) {
        mVerticalSpace = nVerticalSpace
        invalidate()
    }

    fun setHorizontalSpace(nHorizontal: Int) {
        mHorizontal = nHorizontal
        invalidate()
    }

    fun setColumn(nColumn: Int) {
        if (mColumn >= 1) {
            mColumn = nColumn
            mGridLayoutManager.spanCount = nColumn
            invalidate()
        }
    }

    fun setCanScrollHorizontally(bCanScrollHorizontally: Boolean) {
        mGridLayoutManager.setCanScrollHorizontally(bCanScrollHorizontally)
    }

    fun setCanScrollVertically(bCanScrollVertically: Boolean) {
        mGridLayoutManager.setCanScrollVertically(bCanScrollVertically)
    }

    fun setCanScroll(bCanScroll: Boolean) {
        mGridLayoutManager.setCanScroll(bCanScroll)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        mAdapter = adapter
    }

    class GridLayoutManager : androidx.recyclerview.widget.GridLayoutManager {
        private var mCanScroll = true
        private var mCanScrollVertically = true
        private var mCanScrollHorizontally = true

        @JvmOverloads constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        }

        @JvmOverloads constructor(context: Context?, spanCount: Int) : super(context, spanCount) {
        }

        @JvmOverloads constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout) {
        }

        override fun canScrollVertically() = mCanScroll && mCanScrollVertically && super.canScrollVertically()

        override fun canScrollHorizontally() = mCanScroll && mCanScrollHorizontally && super.canScrollHorizontally()

        fun setCanScrollHorizontally(bCanScrollHorizontally: Boolean) {
            mCanScrollHorizontally = bCanScrollHorizontally
        }

        fun setCanScrollVertically(bCanScrollVertically: Boolean) {
            mCanScrollVertically = bCanScrollVertically
        }

        fun setCanScroll(bCanScroll: Boolean) {
            mCanScroll = bCanScroll
        }
    }
}