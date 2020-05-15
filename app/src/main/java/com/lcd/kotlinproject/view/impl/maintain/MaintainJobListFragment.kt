package com.lcd.kotlinproject.view.impl.maintain

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.maintain.MaintainJobListAdapter
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel.Companion.GET_MAINTAIN_JOB_LIST_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel.Companion.GET_MAINTAIN_JOB_LIST_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel.Companion.GET_MAINTAIN_JOB_LIST_SUCCESS
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_SUCCESS
import com.yuwell.androidbase.tool.ProgressDialogUtil
import kotlinx.android.synthetic.main.fragment_maintain_job_list.*

class MaintainJobListFragment : BKFragment() {
    private var mMaintainJobListViewModel: MaintainJobListViewModel? = null
    private var mMaintainJobListAdapter: MaintainJobListAdapter? = null
    private var mProgressDialogUtil: ProgressDialogUtil? = null
    private val PAGE_SIZE = 10
    private var mPage = 1
    private var mMaxPage = 0
    private var mIsFresh = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMaintainJobListAdapter = MaintainJobListAdapter(context!!)
        mProgressDialogUtil = ProgressDialogUtil(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = View.inflate(context, R.layout.fragment_maintain_job_list, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()

        mProgressDialogUtil?.showProgressDialog(R.string.please_wait)
        mMaintainJobListViewModel!!.getOrgData()
    }

    private fun initView(){
        mMaintainJobListAdapter?.setOnItemClickListener(object : MaintainJobListAdapter.OnItemClickListener {
            override fun onItemClick(data: JobListData.Data?) {
                JobListDetail.start(context!!, data)
            }
        })

        recyclerview.adapter = mMaintainJobListAdapter
        recyclerview.layoutManager = LinearLayoutManager(context)

        smartrefreshlayout.setOnRefreshListener {
            mIsFresh = true
            mMaintainJobListAdapter?.clearData()
            mPage = 1
            mMaintainJobListViewModel!!.getWorkOrder(mPage)
        }

        smartrefreshlayout.setOnLoadMoreListener {
            mIsFresh = false
            if (mPage < mMaxPage) {
                mPage++
                mMaintainJobListViewModel!!.getWorkOrder(mPage)
            } else {
                smartrefreshlayout.finishLoadMoreWithNoMoreData()
            }
        }
    }

    private fun initViewModel(){
        mMaintainJobListViewModel = ViewModelProviders.of(this)[MaintainJobListViewModel::class.java]
        mMaintainJobListViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                when (message.what) {
                    GET_MAINTAIN_JOB_LIST_FAIL, GET_MAINTAIN_JOB_LIST_ERROR, GET_ORGUID_FAIL, GET_ORGUID_ERROR -> {
                        mProgressDialogUtil?.dismissProgressDialog()

                        DialogUtil.showConfirmDialog(context, R.string.error, message.obj as String)
                        showDataError()
                    }

                    GET_MAINTAIN_JOB_LIST_SUCCESS ->{
                        message.obj?.let {
                            refreshList(message.obj as JobListData)
                        }
                        message.obj ?: showNoData()
                    }

                    GET_ORGUID_SUCCESS -> {
                        mProgressDialogUtil?.dismissProgressDialog()
                        mMaintainJobListViewModel!!.getWorkOrder(mPage)
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

        if (mMaintainJobListAdapter!!.itemCount == 0) {
            textview_no_data.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun showNoData() {
        if (mIsFresh) {
            smartrefreshlayout.finishRefreshWithNoMoreData()
        } else {
            smartrefreshlayout.finishLoadMoreWithNoMoreData()
        }

        if (mMaintainJobListAdapter!!.itemCount == 0) {
            textview_no_data.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun refreshList(data: JobListData) {
        textview_no_data.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        val nPage: Int = data.ItemTotalCount!! / PAGE_SIZE

        if (nPage > 0) {
            mMaxPage = if (data.ItemTotalCount!! % PAGE_SIZE == 0) nPage else nPage + 1
        } else if (data.ItemTotalCount!! > 0) {
            mMaxPage = 1
        }
        if (!data.List.isNullOrEmpty()) {
            mMaintainJobListAdapter?.setData(data.List)

            if (mIsFresh) {
                smartrefreshlayout.finishRefresh()
            } else {
                smartrefreshlayout.finishLoadMore()
            }
        } else {
            showNoData()
        }
    }
}