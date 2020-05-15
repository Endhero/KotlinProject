package com.lcd.kotlinproject.view.impl.setting

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.lcd.kotlinproject.BuildConfig
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.MainActivity
import com.tencent.bugly.beta.Beta
import com.yuwell.androidbase.view.ToolbarActivity

class SettingsActivity : ToolbarActivity() {
    @BindView(R.id.text_version)
    lateinit var textVersion: TextView
    @BindView(R.id.text_update)
    lateinit var textUpdate: TextView
    @BindView(R.id.imageview_point)
    lateinit var imageview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)

        initView()
    }

    private fun initView() {
        Beta.getUpgradeInfo()?.let {
            imageview.visibility = View.VISIBLE
        }

        textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    override fun getLayoutId() = R.layout.activity_settings

    override fun getTitleId() = R.string.settings

    @OnClick(R.id.layout_about, R.id.layout_update, R.id.text_logout)
    fun onItemClick(view: View) {
        when (view.id) {
            R.id.layout_about -> AboutUs.start(this)
            R.id.layout_update -> Beta.checkUpgrade(true, false)
            R.id.text_logout -> DialogUtil.showConfirmDialog(this, R.string.tip, getString(R.string.log_out),
                DialogInterface.OnClickListener { _, _ -> MainActivity.logoff(this) },
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, SettingsActivity::class.java)
            context.startActivity(starter)
        }
    }
}