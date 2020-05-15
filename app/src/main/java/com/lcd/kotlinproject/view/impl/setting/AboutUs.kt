package com.lcd.kotlinproject.view.impl.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.lcd.kotlinproject.BuildConfig
import com.lcd.kotlinproject.R
import com.yuwell.androidbase.view.ToolbarActivity

class AboutUs : ToolbarActivity() {
    @BindView(R.id.textview_version)
    lateinit var textviewVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ButterKnife.bind(this)

        textviewVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    override fun getLayoutId() =  R.layout.activity_about_us

    override fun getTitleId() = R.string.about_us

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, AboutUs::class.java)
            context.startActivity(starter)
        }
    }
}