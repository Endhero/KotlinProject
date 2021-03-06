package com.lcd.kotlinproject.view.impl.device

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.lcd.base.exception.GlobalError
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.data.model.remote.respose.DeviceStatusData
import com.lcd.kotlinproject.utils.TabUtil
import com.lcd.kotlinproject.view.impl.alarm.Alarm
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.view.widget.SMSCodeInputDialog
import com.lcd.kotlinproject.vm.device.DeviceViewModel
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_device_status.*
import java.util.*

/**
 * Created by Chen on 2020/3/13.
 */
class DeviceStatusActivity : ToolbarActivity() {
    private var tabUtil: TabUtil? = null
    private var vm: DeviceViewModel? = null
    private var warningAdapter: WarningAdapter? = null
    private var mDeviceData: DeviceData? = null
    private var mIsRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        initView()

        vm = ViewModelProviders.of(this).get(DeviceViewModel::class.java)
        vm!!.globalErrorLiveData.observe(this, object : ErrorObserver(this) {
            override fun onChanged(error: GlobalError) {
                dismissProgressDialog()
                showMessage(error.throwable.message)
            }
        })

        vm!!.getDeviceLiveData().observe(this,
            Observer { deviceData: List<DeviceData?> ->
                var tab: TabLayout.Tab
                var dd: DeviceData?
                var ss: SpannableString

                for (i in deviceData.indices) {
                    dd = deviceData[i]

                    if (dd!!.errorCount!! > 0 && i != 0) {
                        ss = SpannableString(dd.name + "(" + dd.errorCount + "个故障)")
                        ss.setSpan(ForegroundColorSpan(-0x4d6d7), dd.name!!.length + 1, dd.name!!.length + dd.errorCount.toString().length + 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        ss = SpannableString(dd.name)
                    }

                    tab = tabUtil!!.addTab(ss)
                    tabUtil!!.updateTab(tab, i == 0)
                    tab.tag = dd

                    if (i == 0) {
                        mDeviceData = dd
                        vm!!.getDeviceState((dd.id)!!)
                    }
                }
            }
        )

        vm!!.getStatusDataLiveData()
            .observe(this, Observer { data: DeviceStatusData ->
                val drawable = if (!data.controlState!!) {
                    text_control.setText(R.string.state_control_false)
                    getDrawable(R.drawable.ic_link)
                } else {
                    text_control.setText(R.string.state_control_true)
                    getDrawable(R.drawable.ic_control)
                }

                drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                text_control.setCompoundDrawables(drawable, null, null, null)
                text_online.isEnabled = (data.onLine)!!
                text_standby.isEnabled = (data.standbyState)!!
                text_control.isEnabled = (data.onLine)!!
                button_reset.isEnabled = (data.onLine)!!
                text_view_more.isEnabled = (data.onLine)!!

                if (data.faultList != null && data.faultList!!.isNotEmpty()) {
                    warningAdapter?.setTextList(data.faultList)
                    view_flipper.visibility = View.VISIBLE
                } else {
                    view_flipper.visibility = View.GONE
                }

                if ((data.onLine)!!) {
                    text_kyj_pressure.text = data.airPressure.toString()
                    text_zyb_pressure.text = data.oxyPressure.toString()
                    text_oxy_concentration.text = data.oxyConcentration.toString()
                    text_running_hour.text = data.compressorDuration.toString()

                    if ((data.oxyState)!!) {
                        if ((data.standbyState)!!) {
                            img_sketch.setImageResource(R.drawable.ic_device_standby)
                        } else {
                            loadRunningSketch()
                        }

                        button_switcher.isEnabled = true
                        button_switcher.isSelected = true
                    } else {
                        img_sketch.setImageResource(R.drawable.ic_device_off)
                        button_switcher.isEnabled = true
                        button_switcher.isSelected = false
                    }
                } else {
                    text_kyj_pressure.setText(R.string.none)
                    text_zyb_pressure.setText(R.string.none)
                    text_oxy_concentration.setText(R.string.none)
                    text_running_hour.setText(R.string.none)
                    img_sketch.setImageResource(R.drawable.ic_device_off)
                    button_switcher.isEnabled = false
                }

                if (mIsRefresh) {
                    smartrefreshlayout.finishRefresh()
                    mIsRefresh = false
                }

                dismissProgressDialog()
            })

        vm!!.getDeviceList(true)
    }

    private fun initView() {
        tabUtil = TabUtil(tab)
        tabUtil?.setOnTabChangeListener(object: TabUtil.OnTabChangeListener {
            override fun onTabSelected(tab: TabLayout.Tab){
                val dd = tab.tag as DeviceData?

                dd?.let {
                    mDeviceData = dd
                    tabUtil?.updateTabText(tab, dd.name)
                    vm!!.getDeviceState((dd.id)!!)
                    showProgressDialog(R.string.loading)
                }
            }
        })

        smartrefreshlayout.setOnRefreshListener(OnRefreshListener {
            mIsRefresh = true
            if (mDeviceData != null) {
                vm!!.getDeviceState((mDeviceData!!.id)!!)
            }
        })
        smartrefreshlayout.setEnableLoadMore(false)
        view_flipper.setInAnimation(this, R.animator.flipper_in)
        view_flipper.setOutAnimation(this, R.animator.flipper_out)
        view_flipper.adapter = WarningAdapter(this).also { warningAdapter = it }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.hasExtra("code")) {
            val code = intent.getStringExtra("code")
            val command = intent.getStringExtra("command")
            Log.d(TAG, "code: $code, command:$command")
            vm!!.control(code, command, this)
        }
    }

    @OnClick(R.id.text_view_more)
    fun viewMore() {
        RealtimeDataActivity.start(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_device_status
    }

    override fun getTitleId(): Int {
        return R.string.device_running
    }

    @OnClick(R.id.button_switcher, R.id.button_reset)
    fun onClick(view: View) {
        when (view.id) {
            R.id.button_switcher -> SMSCodeInputDialog.showDialog(supportFragmentManager, vm!!.getUserMobile() ?: "", if (view.isSelected) "02" else "01")
            R.id.button_reset -> SMSCodeInputDialog.showDialog(supportFragmentManager, vm!!.getUserMobile() ?: "", "03")
        }
    }

    private fun loadRunningSketch() {
        Glide.with(this)
            .load(R.drawable.ic_device_on)
            .placeholder(R.drawable.ic_device_on_loading)
            .into(img_sketch)
    }

    internal class WarningAdapter(private val mContext: Context) : BaseAdapter() {
        private val textList: MutableList<String> = ArrayList()

        override fun getCount() = textList.size

        override fun getItem(position: Int) =  textList[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_tip_warning, null)
                holder = ViewHolder(convertView)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            holder.mWarning.text = textList[position]
            holder.mCheck.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    Alarm.start(mContext)
                }
            })

            return convertView
        }

        fun setTextList(textList: List<String>?) {
            this.textList.clear()
            this.textList.addAll((textList)!!)
            notifyDataSetChanged()
        }

        internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @BindView(R.id.text_warning)
            lateinit var mWarning: TextView
            @BindView(R.id.textview_check)
            lateinit var mCheck: TextView

            init {
                ButterKnife.bind(this, itemView)
            }
        }
    }

    companion object {
        private val TAG = "DeviceStatusActivity"

        fun start(context: Context) {
            val starter = Intent(context, DeviceStatusActivity::class.java)
            context.startActivity(starter)
        }
    }
}