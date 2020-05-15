package com.lcd.kotlinproject.view.adapter.maintain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.view.adapter.base.BaseListAdapter
import com.lcd.kotlinproject.view.viewholder.MaintainJobListViewHolder

class MaintainJobListAdapter(val context: Context):BaseListAdapter<MaintainJobListViewHolder, JobListData.Data>(){
    private var mData : MutableList<JobListData.Data>? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun setData(list: MutableList<JobListData.Data>?) {
        if (mData?.size ?: 0 == 0) {
            mData = list
        } else{
            list?.let {
                for (data in list!!){
                    var contain = false

                    for (dataContain in mData!!){
                        if (data.WorkOrderNum == dataContain.WorkOrderNum){
                            contain = true
                            break
                        }
                    }

                    if (!contain){
                        mData!!.add(data)
                    }
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun clearData() {
        mData?.clear().let { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int) =
        MaintainJobListViewHolder(
            LayoutInflater.from(viewgroup.context)
                .inflate(R.layout.item_maintain_job_list, viewgroup, false)
        )

    override fun getItemCount() = mData?.size ?: 0

    override fun onBindViewHolder(viewholder: MaintainJobListViewHolder, nPosition: Int) {
        val data = mData!![nPosition]

        data.OrderType?.let {
            when {
                it.contains(context.getString(R.string.manual_maintenance))-> viewholder.imageviewType.setImageResource(R.drawable.ic_manual_maintenance)
                it.contains(context.getString(R.string.maintain_plan))-> viewholder.imageviewType.setImageResource(R.drawable.ic_maintain_plan)
            }
        }

        if (data.OrderType!!.contains(context.getString(R.string.manual_maintenance))) {
            viewholder.imageviewType.setImageResource(R.drawable.ic_manual_maintenance)
        } else if (data.OrderType!!.contains(context.getString(R.string.maintain_plan))) {
            viewholder.imageviewType.setImageResource(R.drawable.ic_maintain_plan)
        }

        viewholder.textviewGroup.text = data.DeviceName
        viewholder.textviewName.text = data.OrderType
        viewholder.textviewDealTime.text = context.getString(R.string.deal_time_format, data.DelTime)
        viewholder.textviewContent.text = context.getString(R.string.job_list_content_format, data.Content)

        viewholder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(data)
        }

        if (nPosition == mData!!.size - 1) {
            viewholder.divider.visibility = View.GONE
        } else {
            viewholder.divider.visibility = View.VISIBLE
        }
    }

    fun setOnItemClickListener(onitemclicklistener: OnItemClickListener) {
        mOnItemClickListener = onitemclicklistener
    }

    interface OnItemClickListener {
        fun onItemClick(data: JobListData.Data?)
    }
}