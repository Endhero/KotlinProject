package com.lcd.kotlinproject.view.adapter.alarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.AlarmListData
import com.lcd.kotlinproject.view.adapter.base.BaseListAdapter
import com.lcd.kotlinproject.view.viewholder.AlarmViewHolder

class AlarmListAdapter(var context: Context) : BaseListAdapter<AlarmViewHolder, AlarmListData.AlarmData>() {
    private var mData: MutableList<AlarmListData.AlarmData>? = null

    override fun setData(list: MutableList<AlarmListData.AlarmData>?) {
        if (!list.isNullOrEmpty()) {
            if (mData.isNullOrEmpty()) {
                mData = list
                notifyDataSetChanged()
            } else {
                for (alarmdata in list!!) {
                    var bIsContain = false
                    for (alarmdataContain in mData!!) {
                        if (alarmdataContain.UID.equals(alarmdata.UID)) {
                            bIsContain = true
                            break
                        }
                    }

                    if (!bIsContain) {
                        mData!!.add(alarmdata)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun clearData() {
        if (!mData.isNullOrEmpty()){
            mData!!.clear()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int) =
        AlarmViewHolder(
            LayoutInflater.from(viewgroup.context).inflate(R.layout.item_alarm, viewgroup, false)
        )

    override fun getItemCount() = if(mData.isNullOrEmpty()) 0 else mData!!.size

    override fun onBindViewHolder(alarmviewholder: AlarmViewHolder, nPosition: Int) {
        val alarmdata: AlarmListData.AlarmData = mData!![nPosition]

        alarmviewholder.textViewName.text = alarmdata.DeviceName
        alarmviewholder.textViewWarning.text = alarmdata.Description
        alarmviewholder.textviewWarningDate.text = alarmdata.CreateTime
        alarmviewholder.textviewDealDate.text= alarmdata.ClearTime
        alarmviewholder.imageviewType.setImageResource(getImageByType(alarmdata.ExceptionCode))

        if (alarmdata.ExceptionGrade === 1) {
            alarmviewholder.textviewLevel.setText(R.string.serious)
            alarmviewholder.textviewLevel.setTextColor(context.resources.getColor(R.color.serious))
        } else {
            alarmviewholder.textviewLevel.setText(R.string.common)
            alarmviewholder.textviewLevel.setTextColor(context.resources.getColor(R.color.common))
        }

        if (nPosition == mData!!.size - 1) {
            alarmviewholder.divider.visibility = View.GONE
        } else {
            alarmviewholder.divider.visibility = View.VISIBLE
        }
    }

    private fun getImageByType(str: String?) =
        if (!str.isNullOrEmpty()){
            when (str){
                "M59"-> R.drawable.ic_alarm_button
                "T76"-> R.drawable.ic_alarm_o2
                "01"->  R.drawable.ic_alarm_net
                else -> R.drawable.ic_alarm_machine
            }
        } else{
            R.drawable.ic_alarm_machine
        }
}