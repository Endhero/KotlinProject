package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.lcd.kotlinproject.view.widget.AuthCodeButton

/**
 * 获取验证码按钮
 * Created by Chen on 2015/3/24.
 */
class AuthCodeButton @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatButton(context, attrs, defStyle) {
    private val timer: CountDownTimer
    private var clicked = false
    private var countDownListener: CountDownListener? = null

    fun startCountDown() {
        timer.start()
        clicked = true
    }

    fun stopCountDown() {
        timer.cancel()
        setTextWithLayoutParams("获取验证码")
        isClickable = true
        clicked = false
    }

    fun hasClicked() = clicked

    fun setTextWithLayoutParams(res: String?) {
        gravity = Gravity.CENTER
        text = res
    }

    fun setTextWithLayoutParams(res: Int) {
        gravity = Gravity.CENTER
        setText(res)
    }

    fun setCountDownListener(countDownListener: CountDownListener?) {
        this.countDownListener = countDownListener
    }

    interface CountDownListener {
        fun onCountDownFinish()
    }

    companion object {
        private val TAG= AuthCodeButton::class.java.simpleName
    }

    init {
        gravity = Gravity.CENTER
        timer = object : CountDownTimer(60 * 1000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                setTextWithLayoutParams((millisUntilFinished / 1000).toString() + " s")
                isClickable = false
            }

            override fun onFinish() {
                setTextWithLayoutParams("重新获取")
                isClickable = true
                if (countDownListener != null) {
                    countDownListener!!.onCountDownFinish()
                }
            }
        }
    }
}