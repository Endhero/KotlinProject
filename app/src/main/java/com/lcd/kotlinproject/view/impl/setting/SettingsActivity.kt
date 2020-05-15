package com.lcd.kotlinproject.view.impl.setting

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.lcd.kotlinproject.BuildConfig
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.MainActivity
import com.tencent.bugly.beta.Beta
import com.yuwell.androidbase.view.ToolbarActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : ToolbarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        Beta.getUpgradeInfo()?.let {
            imageview_point.visibility = View.VISIBLE
        }

        text_version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
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