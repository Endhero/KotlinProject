package com.lcd.kotlinproject.view.impl.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.yuwell.androidbase.tool.ToastUtil

open class BKFragment : Fragment() {
    private var unbinder: Unbinder? = null
    private var mToast: ToastUtil? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mToast = ToastUtil(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unbinder = ButterKnife.bind(this, view)
    }

    override fun onDestroyView() {
        unbinder?.unbind()

        super.onDestroyView()
    }

    fun showMessage(nId: Int){
        mToast?.showToast(nId)
    }

    fun showMessage(str: String){
        mToast?.showToast(str)
    }
}