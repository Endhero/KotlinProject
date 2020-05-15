package com.lcd.kotlinproject.view.adapter.maintain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.MaintainRecordData
import com.lcd.kotlinproject.view.adapter.base.BaseListAdapter
import com.lcd.kotlinproject.view.viewholder.MaintainRecordViewHolder

class MaintainRecordAdapter(var  context: Context): BaseListAdapter<MaintainRecordViewHolder, MaintainRecordData.Data>() {
    private var mData: MutableList<MaintainRecordData.Data>? = null

    override fun setData(list: MutableList<MaintainRecordData.Data>?) {
        if (mData?.size ?: 0 == 0){
            mData = list
        } else {
            list?.let {
                for (data in list){
                    var contain = false

                    for (dataContain in mData!!){
                        if (dataContain.DurationTime == data.DurationTime){
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

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int)= MaintainRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_maintain_record, viewgroup, false))

    override fun getItemCount() = mData?.size ?: 0

    override fun onBindViewHolder(maintainrecordviewholder: MaintainRecordViewHolder, nPosition: Int) {
        val (DurationTime, DurationHours, RunHours, MainTainType, Content) = mData!![nPosition]

        if (nPosition == 0) {
            maintainrecordviewholder.viewDividerTop.visibility = View.GONE
        } else {
            maintainrecordviewholder.viewDividerTop.visibility = View.VISIBLE
        }

        maintainrecordviewholder.textviewDate.text = DurationTime
        maintainrecordviewholder.textviewType.text = MainTainType
        maintainrecordviewholder.textviewMaintainRunningTime.text = context.getString(R.string.maintain_running_time_format, RunHours)
        maintainrecordviewholder.textviewMaintainTime.text = context.getString(R.string.maintain_time_format, DurationHours)
        maintainrecordviewholder.textviewMaintainContent.text = context.getString(R.string.maintain_content_format, Content)
    }
}