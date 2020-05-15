package com.lcd.kotlinproject.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import cn.jpush.android.api.JPushInterface
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.view.impl.alarm.Alarm
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.view.impl.camera.CameraList
import com.lcd.kotlinproject.view.impl.device.DeviceStatusActivity
import com.lcd.kotlinproject.view.impl.device.HistoryGraphActivity
import com.lcd.kotlinproject.view.impl.maintain.Maintain
import com.lcd.kotlinproject.view.impl.org.OrgActivity
import com.lcd.kotlinproject.view.impl.setting.SettingsActivity
import com.lcd.kotlinproject.vm.MainViewModel
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.yuwell.androidbase.view.ToolbarActivity
import kotlinx.android.synthetic.main.activity_main.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.util.*

class MainActivity : ToolbarActivity() {
    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0


    private var unreadBadge: Badge? = null
    private var vm: MainViewModel? = null

    companion object {
        fun logoff(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("type", 0)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }

        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        vm!!.getOrgDataLiveData().observe(this, androidx.lifecycle.Observer{ data -> title = data.name })
        vm!!.globalErrorLiveData.observe(this, ErrorObserver(this))
        vm!!.getUnreadAlarmLiveData().observe(this, androidx.lifecycle.Observer{ count: Int -> addCartBadge(count) })
        vm!!.getOrgData()

        val strUser: String = PreferenceSource().getLoginUser()
        val linkedhashset = LinkedHashSet<String>()

        linkedhashset.add(strUser)
        Beta.checkUpgrade(false, false)
        CrashReport.setUserId("Kotlin" + strUser)
        JPushInterface.setTags(this, 0, linkedhashset)
        JPushInterface.setAlias(this, 1, strUser)
        JPushInterface.requestPermission(this)
    }

    override fun onResume() {
        super.onResume()
        vm!!.getUnreadCount()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            SettingsActivity.start(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            super.onBackPressed()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            showMessage(R.string.press_again_exit)
        }
    }

    private fun logoffInternal() {
        vm!!.logoff()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        if (intent.getIntExtra("type", -1) == 0) {
            logoffInternal()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun showNavigation(): Boolean {
        return true
    }

    override fun getNavigationIcon(): Int {
        return R.drawable.ic_setting
    }

    fun onItemClick(v: View) {
        when (v.id) {
            R.id.layout_device_running -> DeviceStatusActivity.start(this)
            R.id.layout_video_monitor -> CameraList.start(this)
            R.id.layout_text_warning -> Alarm.start(this)
            R.id.layout_history_data -> HistoryGraphActivity.start(this)
            R.id.layout_org_info -> OrgActivity.start(this)
            R.id.layout_maintaince -> Maintain.start(this)
        }
    }

    private fun addCartBadge(count: Int) {
        unreadBadge ?: run{
            unreadBadge = QBadgeView(this)
                .setBadgeGravity(Gravity.END or Gravity.TOP)
                .setGravityOffset(0f, -2f, true)
                .setBadgeTextSize(9f, true)
                .setBadgePadding(4f, true)
                .setShowShadow(false)
                .setBadgeBackgroundColor(-0x4edee)
                .bindTarget(img_warning)
        }

        unreadBadge!!.badgeNumber = count
    }
}
