package com.lcd.kotlinproject.view.impl.alarm

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.tabs.TabLayout
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.local.DeviceInfo
import com.lcd.kotlinproject.data.model.remote.respose.NoClearData
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.alarm.AlarmHistoryAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.view.widget.DatePickerTextView
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.CLEAR
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.NOCLEAR
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_DEVICE_LIST_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_DEVICE_LIST_FAIL
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_DEVICE_LIST_SUCCESS
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_NOCLEAR_QUANTITY_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_NOCLEAR_QUANTITY_FAIL
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_NOCLEAR_QUANTITY_SUCCESS
import com.yuwell.androidbase.tool.ResourceUtil

class Alarm : ToolbarActivity() {
    @BindView(R.id.datepickertextview)
    lateinit var datepickertextview: DatePickerTextView
    @BindView(R.id.textview_no_data)
    lateinit var textviewNoData: TextView
    @BindView(R.id.textview_device)
    lateinit var textviewDevice: TextView
    @BindView(R.id.tablayout)
    lateinit var tablayout: TabLayout
    @BindView(R.id.viewpager)
    lateinit var viewpager: ViewPager

    private var mAlarmViewModel: AlarmViewModel? = null
    private var mDateSelected: String = ""
    private var mDeviceInfoSelected: DeviceInfo = DeviceInfo()
    private var mDeviceList: List<DeviceInfo>? = null
    private val mAlarmListFragmentClear: AlarmListFragment = AlarmListFragment()
    private val mAlarmListFragmentNoClear: AlarmListFragment = AlarmListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initViewModel()
        showProgressDialog(R.string.please_wait)

        mDateSelected = ""
        mDeviceInfoSelected = DeviceInfo()
        mAlarmViewModel!!.getDeviceList()
    }

    private fun initView(){
        tablayout.setupWithViewPager(viewpager)

        val arraylistFragment = java.util.ArrayList<Fragment>()
        val arraylistTitle = java.util.ArrayList<String>()

        arraylistFragment.add(mAlarmListFragmentNoClear)
        arraylistFragment.add(mAlarmListFragmentClear)

        arraylistTitle.add(getString(R.string.no_clear_alarm))
        arraylistTitle.add(getString(R.string.clear_alarm))

        val alarmhistoryadapter = AlarmHistoryAdapter(arraylistFragment, arraylistTitle, supportFragmentManager)

        viewpager.adapter = alarmhistoryadapter
        tablayout.getTabAt(intent.getIntExtra("selected", 0))?.select()
        datepickertextview.setOnDateSetListener(object : DatePickerTextView.onDateSetListener {
            override fun onDateSet(nYear: Int, nMonth: Int, nDay: Int) {
                mDateSelected = DateUtil.formatYMD(DateUtil.getDate(nYear, nMonth, nDay))
                refreshFragment()
            }
        })

        datepickertextview.setOnAllCheckListener(object : DatePickerTextView.OnAllCheckListener {
            override fun onAllCheck() {
                mDateSelected = ""
                refreshFragment()
            }
        })
    }

    private fun initViewModel() {
        mAlarmViewModel = ViewModelProviders.of(this)[AlarmViewModel::class.java]
        mAlarmViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                dismissProgressDialog()
                when (message.what) {
                    GET_DEVICE_LIST_SUCCESS -> {
                        val list = message.obj as List<DeviceInfo>

                        if (!list.isNullOrEmpty()) {
                            mDeviceList = list
                            refreshFragment()
                        } else {
                            Toast.makeText(this, R.string.no_device, Toast.LENGTH_LONG).show()
                            showNoData()
                        }
                    }

                    GET_DEVICE_LIST_FAIL, GET_DEVICE_LIST_ERROR -> {
                        DialogUtil.showConfirmDialog(this, R.string.error, message.obj as String)
                        showNoData()
                    }

                    GET_NOCLEAR_QUANTITY_SUCCESS -> {
                        val nocleardata: NoClearData = message.obj as NoClearData

                        if (nocleardata.NoClearNum !== 0) {
                            tablayout.getTabAt(0)!!.text = getString(R.string.no_clear_alarm_format, nocleardata.NoClearNum.toString() + "")
                        } else {
                            tablayout.getTabAt(0)!!.text = getString(R.string.no_clear_alarm)
                        }
                    }
                    GET_NOCLEAR_QUANTITY_FAIL, GET_NOCLEAR_QUANTITY_ERROR -> {
                    }
                }
            })

        mAlarmViewModel!!.globalErrorLiveData.observe(this, Observer{ error ->
            dismissProgressDialog()
            showMessage(ResourceUtil.getStringId(application, "error_" + error.type))
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        mDateSelected = ""
        mDeviceInfoSelected = DeviceInfo(getString(R.string.all), "")
        datepickertextview.text = getString(R.string.all)
        textviewDevice.text = getString(R.string.all)

        refreshFragment()
    }

    @OnClick(R.id.textview_device)
    fun onClick(view: View?) {
        val strDevice: Array<String?>
        var nSelected = 0

        if (!mDeviceList.isNullOrEmpty()) {
            strDevice = arrayOfNulls(mDeviceList!!.size + 1)
            strDevice[0] = getString(R.string.all)

            for (i in mDeviceList!!.indices) {
                strDevice[i + 1] = mDeviceList!![i].DeviceName

                if (mDeviceInfoSelected != null && mDeviceInfoSelected.DeviceUid.equals(mDeviceList!![i].DeviceUid)) {
                    nSelected = i + 1
                }
            }
        } else return


        if (nSelected == 0) {
            mDeviceInfoSelected = DeviceInfo(getString(R.string.all), "")
        }

        val itemClickListener = DialogInterface.OnClickListener { _, nWhich ->
                if (mDeviceList != null) {
                    mDeviceInfoSelected = if (nWhich == 0)  DeviceInfo(getString(R.string.all), "") else mDeviceList!![nWhich - 1]
                }
            }
        val negativeClickListener = DialogInterface.OnClickListener { dialog, _-> dialog.dismiss() }
        val positiveClickListener = DialogInterface.OnClickListener { _, _->
                textviewDevice!!.text = mDeviceInfoSelected.DeviceName
                refreshFragment()
            }

        DialogUtil.showListDialog(this@Alarm, R.string.please_select_device, strDevice, nSelected, itemClickListener, negativeClickListener, positiveClickListener)
    }

    private fun showNoData() {
        textviewNoData.visibility = View.VISIBLE
        viewpager.visibility = View.GONE
        datepickertextview.isEnabled = false
    }

    override fun getLayoutId() = R.layout.activity_alarm

    override fun getTitleId() = R.string.alarm

    private fun refreshFragment() {
        mAlarmViewModel!!.getNoReadQuantity(mDateSelected, mDeviceInfoSelected.DeviceUid)
        mAlarmListFragmentNoClear.refresh(mDateSelected, mDeviceInfoSelected.DeviceUid ?: "", NOCLEAR)
        mAlarmListFragmentClear.refresh(mDateSelected, mDeviceInfoSelected.DeviceUid ?: "", CLEAR)
    }

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, Alarm::class.java)
            context.startActivity(starter)
        }
    }
}