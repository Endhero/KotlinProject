package com.lcd.kotlinproject.view.impl.base

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import butterknife.ButterKnife
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.SoftKeyBoardListener
import com.yuwell.androidbase.view.ToolbarActivity

abstract class ToolbarActivity : ToolbarActivity() {
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)

        val textviewTitle = findViewById<TextView>(R.id.text_title)
        textviewTitle?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        if (needSoftKeyBoardMonitor()) {
            mHandler = Handler(Looper.getMainLooper())

            SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    onKeyBoardShow()
                }

                override fun keyBoardHide(height: Int) {
                    mHandler?.postDelayed({ onKeyBoardHide() }, 50)
                }
            })
        }
    }

    override fun getNavigationIcon() = R.drawable.ic_arrow_back_black

    protected open fun onKeyBoardShow() {}

    protected open fun onKeyBoardHide() {}

    protected open fun needSoftKeyBoardMonitor() = false

    override fun setToolbar() {
        super.setToolbar()
    }

    override fun onStart() {
        super.onStart()

        toolbar?.isFocusable = true
        toolbar?.isFocusableInTouchMode = true
        toolbar?.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()

        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
    }
}