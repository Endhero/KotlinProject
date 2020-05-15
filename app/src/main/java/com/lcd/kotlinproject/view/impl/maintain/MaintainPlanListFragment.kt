package com.lcd.kotlinproject.view.impl.maintain

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.maintain.MaintainPlanListAdapter
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_MAINTAIN_PLAN_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_MAINTAIN_PLAN_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_MAINTAIN_PLAN_SUCCESS
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_FAIL
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yuwell.androidbase.tool.ProgressDialogUtil

class MaintainPlanListFragment : BKFragment(){
    @BindView(R.id.recyclerview)
    lateinit var recyclerview: RecyclerView
    @BindView(R.id.textview_no_data)
    lateinit var textviewNoData: TextView
    @BindView(R.id.smartrefreshlayout)
    lateinit var smartrefreshlayout: SmartRefreshLayout

    private var mMaintainPlanListAdapter: MaintainPlanListAdapter? = null
    private var mMaintainPlanListViewModel: MaintainPlanListViewModel? = null
    private val mProgressDialogUtil: ProgressDialogUtil = ProgressDialogUtil(context)
    private var mIsRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMaintainPlanListAdapter = MaintainPlanListAdapter(context!!)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)=  View.inflate(context, R.layout.fragment_maintain_plan_list, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()

        mProgressDialogUtil.showProgressDialog(R.string.please_wait)
        mMaintainPlanListViewModel!!.getOrgData()
    }

    private fun initView(){
        mMaintainPlanListAdapter!!.setOnItemClickListener(object : MaintainPlanListAdapter.OnItemClickListener {
            override fun onItemClick(maintaindata: MaintainData?) {
                MaintainRecord.start(context!!, maintaindata)
            }
        })

        smartrefreshlayout.setOnRefreshListener {
            mIsRefresh = true
            mMaintainPlanListViewModel!!.getOrgData()
        }

        smartrefreshlayout.setEnableLoadMore(false)

        recyclerview.adapter = mMaintainPlanListAdapter
        recyclerview.layoutManager = LinearLayoutManager(context)
    }

    private fun initViewModel(){
        mMaintainPlanListViewModel = ViewModelProviders.of(this)[MaintainPlanListViewModel::class.java]
        mMaintainPlanListViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                mProgressDialogUtil.dismissProgressDialog()
                when (message.what) {
                    GET_MAINTAIN_PLAN_FAIL, GET_MAINTAIN_PLAN_ERROR, GET_ORGUID_FAIL, GET_ORGUID_ERROR -> {
                        DialogUtil.showConfirmDialog(context, R.string.error, message.obj as String)
                        showNoData()
                    }

                    GET_MAINTAIN_PLAN_SUCCESS -> if (message.obj != null) {
                        refreshList(message.obj as MutableList<MaintainData>)
                    } else {
                        showNoData()
                    }
                }
            })
    }

    private fun showNoData() {
        if (mIsRefresh) {
            smartrefreshlayout.finishRefresh(false)
        }

        textviewNoData.visibility = View.VISIBLE
        recyclerview.visibility = View.GONE
    }

    private fun refreshList(list: MutableList<MaintainData>) {
        if (list.isNotEmpty()) {
            mMaintainPlanListAdapter?.clearData()
            mMaintainPlanListAdapter?.setData(list)
            textviewNoData.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE

            if (mIsRefresh) {
                smartrefreshlayout.finishRefresh()
            }
        } else {
            if (mIsRefresh) {
                smartrefreshlayout.finishRefreshWithNoMoreData()
            }

            textviewNoData.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }
}