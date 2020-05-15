package com.lcd.kotlinproject.view.adapter.maintain

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.view.adapter.base.BaseListAdapter
import com.lcd.kotlinproject.view.viewholder.MaintainViewHolder

class MaintainPlanListAdapter(val context: Context) : BaseListAdapter<MaintainViewHolder, MaintainData> (){
    private var mData: MutableList<MaintainData>? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun setData(list: MutableList<MaintainData>?) {
        if (mData?.size ?: 0 == 0){
            mData = list
        } else {
            list?.let {
                for (data in list) {
                    var contain = false

                    for (dataContain in mData!!){
                        if (dataContain.OrgUID == data.OrgUID){
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
        MaintainViewHolder(
            LayoutInflater.from(viewgroup.context)
                .inflate(R.layout.item_maintain_plan, viewgroup, false)
        )

    override fun getItemCount() = mData?.size ?: 0

    override fun onBindViewHolder(maintainviewholder: MaintainViewHolder, nPosition: Int) {
        val maintaindata = mData!![nPosition]

        maintaindata.EquipmentName?.let {
            when {
                it.contains(context.getString(R.string.air_compressor)) -> maintainviewholder.imageviewType.setImageResource(R.drawable.ic_air_compressor)
                it.contains(context.getString(R.string.booster_pump)) -> maintainviewholder.imageviewType.setImageResource(R.drawable.ic_booster_pump)
                it.contains(context.getString(R.string.filter)) -> maintainviewholder.imageviewType.setImageResource(R.drawable.ic_filter)
            }
        }

        maintainviewholder.textviewGroup.text = maintaindata.DeviceName
        maintainviewholder.textviewName.text = maintaindata.EquipmentName
        maintainviewholder.textviewMaintainTimeRemain.text = context.getString(R.string.time_remain_format, maintaindata.SurplusHours)
        maintainviewholder.textviewTime.text = context.getString(R.string.time_format, maintaindata.DurationTime)
        maintainviewholder.textviewType.text = context.getString(R.string.type_format, maintaindata.MainTainType)
        maintainviewholder.textviewContent.text = context.getString(R.string.content_format, maintaindata.Content)

        maintainviewholder.textviewMore.setOnClickListener{
            mOnItemClickListener?.onItemClick(maintaindata)
        }
    }

    fun setOnItemClickListener(onitemclicklistener: OnItemClickListener) {
        mOnItemClickListener = onitemclicklistener
    }

    interface OnItemClickListener {
        fun onItemClick(maintaindata: MaintainData?)
    }
}