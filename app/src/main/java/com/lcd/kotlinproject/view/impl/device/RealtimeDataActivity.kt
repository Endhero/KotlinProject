package com.lcd.kotlinproject.view.impl.device

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.utils.TabUtil
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.vm.device.DeviceViewModel
import com.yuwell.androidbase.view.ToolbarActivity
import java.lang.String

class RealtimeDataActivity : ToolbarActivity() {
    @BindView(R.id.tab)
    lateinit var mDeviceTab: TabLayout
    @BindView(R.id.text_update_time)
    lateinit var mUpdateTime: TextView
    @BindView(R.id.text_oxy_concentration)
    lateinit var mOxyConcentration: TextView
    @BindView(R.id.text_kyj_pressure)
    lateinit var mKyjPressure: TextView
    @BindView(R.id.text_zyb_pressure)
    lateinit var mZybPressure: TextView
    @BindView(R.id.text_oxy_pressure)
    lateinit var mOxyPressure: TextView
    @BindView(R.id.text_oxy_production)
    lateinit var mOxyProduction: TextView
    @BindView(R.id.text_oxy_usage)
    lateinit var mOxyUsage: TextView
    @BindView(R.id.text_kyj_duration)
    lateinit var mKyjDuration: TextView
    @BindView(R.id.text_zyb_duration)
    lateinit var mZybDuration: TextView
    @BindView(R.id.text_status_update_time)
    lateinit var mStatusUpdateTime: TextView
    @BindView(R.id.text_kyj_status)
    lateinit var mKyjStatus: TextView
    @BindView(R.id.text_zyb_status)
    lateinit var mZybStatus: TextView
    @BindView(R.id.text_emergency_stop)
    lateinit var mEmergencyStop: TextView
    @BindView(R.id.text_kyj_switch)
    lateinit var mKyjSwitch: TextView
    @BindView(R.id.text_zyb_switch)
    lateinit var mZybSwitch: TextView
    @BindView(R.id.text_alert)
    lateinit var mAlert: TextView
    @BindView(R.id.text_main_oxy)
    lateinit var mMainOxy: TextView
    @BindView(R.id.text_oxy)
    lateinit var mOxy: TextView
    @BindView(R.id.text_oxy_generate)
    lateinit var mOxyGenerate: TextView
    @BindView(R.id.text_oxy_standby)
    lateinit var mOxyStandby: TextView
    @BindView(R.id.text_reset)
    lateinit var mReset: TextView
    @BindView(R.id.text_oxy_control)
    lateinit var mOxyControl: TextView

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
            mOxyConcentration.text = String.valueOf(data.oxyConcentration)
            mKyjPressure.text = String.valueOf(data.airPressure)
            mZybPressure.text = String.valueOf(data.inletPressure)
            mOxyPressure.text = String.valueOf(data.oxyPressure)
            mOxyProduction.text = String.valueOf(data.averageFlow)
            mOxyUsage.text = String.valueOf(data.totalFlow)
            mKyjDuration.text = String.valueOf(data.compressorDuration)
            mZybDuration.text = String.valueOf(data.pumpDuration)
            mKyjStatus.setText(if (data.compressorState!!) R.string.state_ok else R.string.state_error)
            mZybStatus.setText(if (data.boosterPumpState!!) R.string.state_ok else R.string.state_error)
            mEmergencyStop.setText(if (data.stopButton!!) R.string.state_ok else R.string.state_pressed)
            mKyjSwitch.setText(if (data.compressorSwitch!!) R.string.state_on else R.string.state_off)
            mZybSwitch.setText(if (data.boosterPumpSwitch!!) R.string.state_on else R.string.state_off)
            mAlert.setText(if (data.alert!!) R.string.state_alert_on else R.string.state_alert_off)
            mMainOxy.setText(if (data.hostState!!) R.string.state_on else R.string.state_off)
            mOxy.setText(if (data.oxyState!!) R.string.state_on else R.string.state_off)
            mOxyGenerate.setText(if (data.modeState!!) R.string.state_manual else R.string.state_auto)
            mOxyStandby.setText(if (data.standbyState!!) R.string.state_standby_true else R.string.state_standby_false)
            mReset.setText(if (data.resetVal!!) R.string.state_reset_true else R.string.state_reset_false)
            mOxyControl.setText(if (data.controlState!!) R.string.state_control_true else R.string.state_control_false)
            mUpdateTime.text = DateUtil.formatYMDHMS(data.updateTime!!)
            mStatusUpdateTime.text = DateUtil.formatYMDHMS(data.updateTime!!)
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
        tabUtil = TabUtil(mDeviceTab)
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