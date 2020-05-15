package com.lcd.kotlinproject.view.impl.maintain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.data.model.remote.respose.MaintainRecordData
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.maintain.MaintainRecordAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel.Companion.GET_MAINTAIN_RECORD_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel.Companion.GET_MAINTAIN_RECORD_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel.Companion.GET_MAINTAIN_RECORD_SUCCESS
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread
import java.util.concurrent.Future

class MaintainRecord : ToolbarActivity() {
    @BindView(R.id.recyclerview)
    lateinit var recyclerview: RecyclerView
    @BindView(R.id.textview_no_data)
    lateinit var textviewNoData: TextView
    @BindView(R.id.textview_group)
    lateinit var textviewGroup: TextView
    @BindView(R.id.textview_name)
    lateinit var textviewName: TextView
    @BindView(R.id.smartrefreshlayout)
    lateinit var smartrefreshlayout: SmartRefreshLayout

    private val PAGE_SIZE = 10
    private var mMaintainRecordAdapter: MaintainRecordAdapter = MaintainRecordAdapter(this)
    private var mMaintainRecordViewModel: MaintainRecordViewModel? = null
    private var mPage = 1
    private var mMaxPage = 0
    private var mIsFresh = true
    private var mMaintainData: MaintainData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initViewModel()

        showProgressDialog(R.string.please_wait)
        mMaintainRecordViewModel!!.getMaintainRecord(mMaintainData!!, mPage)
    }

    private fun initView(){
        mMaintainData = intent.getSerializableExtra("data") as MaintainData?
        mMaintainData?.let {
            textviewGroup.text = mMaintainData!!.DeviceName

            when (mMaintainData!!.EquipmentType) {
                "0" -> textviewName.setText(R.string.air_compressor)
                "1" -> textviewName.setText(R.string.booster_pump)
                "2" -> textviewName.setText(R.string.filter)
                else -> {}
            }

            recyclerview.adapter = mMaintainRecordAdapter
            recyclerview.layoutManager = LinearLayoutManager(this)

            smartrefreshlayout.setOnRefreshListener {
                mIsFresh = true
                mMaintainRecordAdapter.clearData()
                mPage = 1
                mMaintainRecordViewModel!!.getMaintainRecord(mMaintainData!!, mPage)
            }

            smartrefreshlayout.setOnLoadMoreListener{
                mIsFresh = false

                if (mPage < mMaxPage) {
                    mPage++
                    mMaintainRecordViewModel!!.getMaintainRecord(mMaintainData!!, mPage)
                } else {
                    smartrefreshlayout.finishLoadMoreWithNoMoreData()
                }
            }
        }
    }

    private fun initViewModel(){
        mMaintainRecordViewModel = ViewModelProviders.of(this)[MaintainRecordViewModel::class.java]
        mMaintainRecordViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                dismissProgressDialog()
                when (message.what) {
                    GET_MAINTAIN_RECORD_FAIL, GET_MAINTAIN_RECORD_ERROR -> {
                        DialogUtil.showConfirmDialog(this, R.string.error, message.obj as String)
                        showDataError()
                    }
                    GET_MAINTAIN_RECORD_SUCCESS -> if (message.obj != null) {
                        refreshList(message.obj as MaintainRecordData)
                    } else {
                        showNoData()
                    }
                }
            })
    }

    private fun showDataError() {
        if (mIsFresh) {
            smartrefreshlayout.finishRefresh(false)
        } else {
            smartrefreshlayout.finishLoadMore(false)
        }

        if (mMaintainRecordAdapter.itemCount == 0) {
            textviewNoData.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun showNoData() {
        if (mIsFresh) {
            smartrefreshlayout.finishRefreshWithNoMoreData()
        } else {
            smartrefreshlayout.finishLoadMoreWithNoMoreData()
        }

        if (mMaintainRecordAdapter.itemCount == 0) {
            textviewNoData.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun refreshList(data: MaintainRecordData) {
        textviewNoData.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        val nPage: Int = data.ItemTotalCount ?: 0 / PAGE_SIZE

        if (nPage > 0) {
            mMaxPage = if (data.ItemTotalCount ?: 0 % PAGE_SIZE == 0) nPage else nPage + 1
        } else if (data.ItemTotalCount ?: 0 > 0) {
            mMaxPage = 1
        }

        if (!data.List.isNullOrEmpty()) {
            mMaintainRecordAdapter.setData(data.List)

            if (mIsFresh) {
                smartrefreshlayout.finishRefresh()
            } else {
                smartrefreshlayout.finishLoadMore()
            }
        } else {
            showNoData()
        }
    }

    override fun getLayoutId()=  R.layout.activity_maintain_record

    override fun getTitleId() = R.string.maintain_record

    companion object{
        fun start(context: Context, data: MaintainData?) {
            val starter = Intent(context, MaintainRecord::class.java)
            starter.putExtra("data", data)
            context.startActivity(starter)
        }
    }
}