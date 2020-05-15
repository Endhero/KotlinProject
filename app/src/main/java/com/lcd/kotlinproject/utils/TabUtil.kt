package com.lcd.kotlinproject.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.text.TextUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lcd.kotlinproject.R

/**
 * Created by Chen on 2020/3/16.
 */
class TabUtil(private val mTabLayout: TabLayout) {
    private var onTabChangeListener: OnTabChangeListener? = null

    fun addTab(tabName: CharSequence?): TabLayout.Tab {
        val tab = mTabLayout.newTab()
        tab.setCustomView(R.layout.tab_custom)
        val custom = tab.customView as TextView?
        custom?.text = tabName
        mTabLayout.addTab(tab)
        return tab
    }

    fun updateTab(tab: TabLayout.Tab, selected: Boolean) {
        tab.customView ?: return

        val textView = tab.customView as TextView
        val animator: ValueAnimator

        if (selected) {
            animator = ObjectAnimator.ofFloat(textView, "TextSize", 12f, 20f).setDuration(200)
            animator.setInterpolator(DecelerateInterpolator())
            animator.start()
        } else {
            animator = ObjectAnimator.ofFloat(textView, "TextSize", 20f, 12f).setDuration(200)
            animator.setInterpolator(DecelerateInterpolator())
            animator.start()
        }
    }

    fun updateTabText(tab: TabLayout.Tab, newText: CharSequence?) {
        tab.customView ?: return

        val mCustom = tab.customView as TextView

        if (mCustom.text != newText){
            mCustom.text = newText
        }
    }

    fun setOnTabChangeListener(onTabChangeListener: OnTabChangeListener?) {
        this.onTabChangeListener = onTabChangeListener
    }

    interface OnTabChangeListener {
        fun onTabSelected(tab: TabLayout.Tab)
    }

    init {
        mTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTab(tab, true)
                if (onTabChangeListener != null) {
                    onTabChangeListener!!.onTabSelected(tab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}