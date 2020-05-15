package com.lcd.kotlinproject.view.impl.device

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.lcd.kotlinproject.BuildConfig
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.view.widget.ConditionsSelect
import com.lcd.kotlinproject.view.widget.DatePickerDialog
import com.lcd.kotlinproject.vm.device.DeviceViewModel
import com.yuwell.androidbase.view.ToolbarActivity
import com.yuwell.androidbase.web.ArgsBuilder
import com.yuwell.androidbase.web.WebViewFragment
import kotlinx.android.synthetic.main.activity_history_graph.*
import java.util.*

class HistoryGraphActivity : ToolbarActivity() {
    private val calendar = Calendar.getInstance()
    private var vm: DeviceViewModel? = null
    private var fragment: WebViewFragment? = null
    private var deviceId: String? = null
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)

        vm = ViewModelProviders.of(this)[DeviceViewModel::class.java]
        vm!!.getDeviceLiveData().observe(this, androidx.lifecycle.Observer{ deviceData ->
            if (!deviceData.isNullOrEmpty()) {
                conditions_select.setRightText(deviceData[0]?.name)
                deviceId = deviceData[0]?.id
                loadFragment(savedInstanceState, deviceId, DateUtil.formatYMD(calendar.time))
            }
        })

        conditions_select.setLeftText(DateUtil.formatYMD(calendar.time))
        conditions_select.setOnItemClickListener(object : ConditionsSelect.OnItemClickListener{
            override fun onLeftClick() {
                val dialog = DatePickerDialog(this@HistoryGraphActivity)
                dialog.init(calendar.time, object : DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(nYear: Int, nMonth: Int, nDay: Int){
                        calendar.set(nYear, nMonth - 1, nDay)
                        conditions_select.setLeftText(DateUtil.formatYMD(calendar.time))
                        fragment?.wvjbWebView?.loadUrl(BuildConfig.HOST.toString() + "HistoryDataMobile?DeviceID="+ deviceId + "&StartDate=" + DateUtil.formatYMD(calendar.time))
                    }
                }, null)
                dialog.setTitle(R.string.please_select_date)
                dialog.hideCheckbox()
                dialog.show()
            }

            override fun onRightClick() {
                val nPosition = position
                val builder = AlertDialog.Builder(this@HistoryGraphActivity)
                builder.setSingleChoiceItems(vm!!.getDeviceNameArray(), position) { _, which -> position = which }
                builder.setTitle(R.string.please_select_device)
                builder.setPositiveButton(R.string.ensure) { dialog, _ ->
                    conditions_select.setRightText(vm!!.getDeviceNameArray()?.get(position))
                    deviceId = vm!!.getDeviceId(position)
                    fragment?.wvjbWebView?.loadUrl(BuildConfig.HOST.toString() + "HistoryDataMobile?DeviceID=" + deviceId + "&StartDate=" + DateUtil.formatYMD(calendar.time))
                    dialog.dismiss()
                }
                builder.setNegativeButton(R.string.cancel) { dialog, _ ->
                    position = nPosition
                    dialog.dismiss()
                }
                builder.show()
            }
        })

        vm!!.getDeviceList(false)
    }

    private fun loadFragment(savedInstanceState: Bundle?, id: String?, time: String) {
        if (savedInstanceState == null) {
            fragment = WebViewFragment()
            fragment?.arguments = ArgsBuilder()
                .setUrl(BuildConfig.HOST.toString() + "HistoryDataMobile?DeviceID=" + id + "&StartDate=" + time)
                .setHeader(getHeader())
                .create()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment!!, "web")
                .commitAllowingStateLoss()
        }
    }

    override fun getLayoutId() = R.layout.activity_history_graph

    override fun getTitleId() =  R.string.history_data

    private fun getHeader(): HashMap<String, String>? {
        val preferenceSource = PreferenceSource()
        val header = HashMap<String, String>()

        header["Authorization"] = preferenceSource.getAuthorization()

        return header
    }

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, HistoryGraphActivity::class.java)
            context.startActivity(starter)
        }
    }
}