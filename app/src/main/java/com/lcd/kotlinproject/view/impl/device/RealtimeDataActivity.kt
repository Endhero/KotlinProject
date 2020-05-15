package com.lcd.kotlinproject.view.impl.device

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.utils.TabUtil
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.vm.device.DeviceViewModel
import com.yuwell.androidbase.view.ToolbarActivity
import kotlinx.android.synthetic.main.activity_real_time_data.*
import java.lang.String

class RealtimeDataActivity : ToolbarActivity() {
    private var tabUtil: TabUtil? = null
    private var vm: DeviceViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        initView()

        vm = ViewModelProviders.of(this)[(DeviceViewModel::class.java)]
        vm!!.globalErrorLiveData.observe(this, ErrorObserver(this))
        vm!!.getDeviceLiveData().observe(this, Observer{ deviceData ->
            var tab: TabLayout.Tab
            var dd: DeviceData?

            for (i in deviceData.indices) {
                dd = deviceData[i]
                tab = tabUtil?.addTab(dd?.name)!!
                tab.tag = dd
                tabUtil?.updateTab(tab, i == 0)

                if (i == 0) {
                    dd?.id?.let { vm!!.getDeviceState(it) }
                }
            }
        })

        vm!!.getStatusDataLiveData().observe(this, Observer{ data ->
            text_oxy_concentration.text = String.valueOf(data.oxyConcentration)
            text_kyj_pressure.text = String.valueOf(data.airPressure)
            text_zyb_pressure.text = String.valueOf(data.inletPressure)
            text_oxy_pressure.text = String.valueOf(data.oxyPressure)
            text_oxy_production.text = String.valueOf(data.averageFlow)
            text_oxy_usage.text = String.valueOf(data.totalFlow)
            text_kyj_duration.text = String.valueOf(data.compressorDuration)
            text_zyb_duration.text = String.valueOf(data.pumpDuration)
            text_kyj_status.setText(if (data.compressorState!!) R.string.state_ok else R.string.state_error)
            text_zyb_status.setText(if (data.boosterPumpState!!) R.string.state_ok else R.string.state_error)
            text_emergency_stop.setText(if (data.stopButton!!) R.string.state_ok else R.string.state_pressed)
            text_kyj_switch.setText(if (data.compressorSwitch!!) R.string.state_on else R.string.state_off)
            text_zyb_switch.setText(if (data.boosterPumpSwitch!!) R.string.state_on else R.string.state_off)
            text_alert.setText(if (data.alert!!) R.string.state_alert_on else R.string.state_alert_off)
            text_main_oxy.setText(if (data.hostState!!) R.string.state_on else R.string.state_off)
            text_oxy.setText(if (data.oxyState!!) R.string.state_on else R.string.state_off)
            text_oxy_generate.setText(if (data.modeState!!) R.string.state_manual else R.string.state_auto)
            text_oxy_standby.setText(if (data.standbyState!!) R.string.state_standby_true else R.string.state_standby_false)
            text_reset.setText(if (data.resetVal!!) R.string.state_reset_true else R.string.state_reset_false)
            text_oxy_control.setText(if (data.controlState!!) R.string.state_control_true else R.string.state_control_false)
            text_update_time.text = DateUtil.formatYMDHMS(data.updateTime!!)
            text_status_update_time.text = DateUtil.formatYMDHMS(data.updateTime!!)
        })

        vm!!.getDeviceList(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.real_time_data, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_history) {
            HistoryGraphActivity.start(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        tabUtil = TabUtil(tab)
        tabUtil?.setOnTabChangeListener(object : TabUtil.OnTabChangeListener {
            override fun onTabSelected(tab: TabLayout.Tab){
                val dd: DeviceData? = tab.tag as DeviceData?
                if (dd != null) {
                    vm!!.getDeviceState(dd.id!!)
                }
            }
        })
    }

    override fun getLayoutId() = R.layout.activity_real_time_data

    override fun getTitleId() =  R.string.realtime_data

    companion object{
        private const val TAG = "RealtimeDataActivity"

        fun start(context: Context) {
            val starter = Intent(context, RealtimeDataActivity::class.java)
            context.startActivity(starter)
        }
    }
}