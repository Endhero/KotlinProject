package com.lcd.kotlinproject.view.impl.alarm

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.AlarmListData
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.alarm.AlarmListAdapter
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.SEARCH_ALARM_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.SEARCH_ALARM_FAIL
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.SEARCH_ALARM_SUCCESS
import com.yuwell.androidbase.tool.ResourceUtil
import kotlinx.android.synthetic.main.fragment_alarm_list.*

class AlarmListFragment : BKFragment() {
    private var mAlarmListViewModel: AlarmListViewModel? = null
    private var mAlarmListAdapter: AlarmListAdapter? = null
    private var mDate: String? = null
    private var mDeviceUID: String? = null
    private var mType = 0
    private var mPage = 1
    private var mIsFresh = false

    companion object{
        private const val PAGE_SIZE = 20
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_alarm_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initView(){
        mAlarmListAdapter = AlarmListAdapter(context!!);

        smartrefreshlayout.setOnRefreshListener {
            mAlarmListAdapter?.clearData()
            refresh(mDate ?: "", mDeviceUID?: "", mType)
        }

        smartrefreshlayout.setOnLoadMoreListener { loadMore() }

        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerview.adapter = mAlarmListAdapter
    }

    private fun initViewModel(){
        mAlarmListViewModel = ViewModelProviders.of(this).get(AlarmListViewModel::class.java)
        mAlarmListViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                when (message.what) {
                    SEARCH_ALARM_SUCCESS -> {
                        val alarmlistdata: AlarmListData = message.obj as AlarmListData
                        val list: MutableList<AlarmListData.AlarmData>? = alarmlistdata.List

                        if (list!!.isNotEmpty()) {
                            if (mIsFresh) {
                                smartrefreshlayout.finishRefresh(true)
                                refreshList(list)
                            } else {
                                val nTotal: Int = (mPage - 1) * PAGE_SIZE + list.size

                                if (nTotal == alarmlistdata.ItemTotalCount) {
                                    smartrefreshlayout.finishLoadMoreWithNoMoreData()
                                } else {
                                    smartrefreshlayout.finishLoadMore(true)
                                }

                                refreshList(list)
                            }
                        } else {
                            if (mIsFresh) {
                                smartrefreshlayout.finishRefreshWithNoMoreData()
                                showNoData()
                            } else {
                                smartrefreshlayout.finishLoadMoreWithNoMoreData()
                            }
                        }
                    }

                    SEARCH_ALARM_FAIL, SEARCH_ALARM_ERROR -> {
                        if (mIsFresh) {
                            smartrefreshlayout.finishRefresh(false)
                        } else {
                            smartrefreshlayout.finishLoadMore(false)
                        }

                        showNoData()
                        DialogUtil.showConfirmDialog(context, R.string.error, message.obj as String)
                    }
                }
            })

        mAlarmListViewModel?.globalErrorLiveData?.observe(this,
            Observer{error ->
                showMessage(ResourceUtil.getStringId(activity?.application, "error_" + error.type))
            })
    }

    fun refresh(strDate: String, strDeviceUID: String, nType: Int) {
        mDate = strDate
        mDeviceUID = strDeviceUID
        mType = nType
        mPage = 1
        mIsFresh = true
        mAlarmListAdapter?.clearData()
        mAlarmListViewModel!!.searchAlarm(strDate, strDeviceUID, nType, mPage, PAGE_SIZE)
    }

    private fun loadMore() {
        mIsFresh = false
        mAlarmListViewModel!!.searchAlarm(mDate ?: "", mDeviceUID ?: "", mType, mPage, PAGE_SIZE)
    }

    private fun showNoData() {
        if (mAlarmListAdapter!!.itemCount == 0) {
            mAlarmListAdapter?.clearData()
            textview_no_data.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun refreshList(list: MutableList<AlarmListData.AlarmData>) {
        if (list.size == PAGE_SIZE) {
            mPage++
        }

        mAlarmListAdapter?.setData(list)
        textview_no_data.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE
    }
}