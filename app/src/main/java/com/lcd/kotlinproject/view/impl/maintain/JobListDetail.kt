package com.lcd.kotlinproject.view.impl.maintain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.view.adapter.widget.PictureAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import kotlinx.android.synthetic.main.activity_job_list_detail.*
import java.util.*

class JobListDetail : ToolbarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView(){
        val data = intent.getSerializableExtra("data") as JobListData.Data

        data?.let {
            textview_group_value.text = it.DeviceName
            textview_job_list_type_value.text = it.OrderType
            textview_job_list_code_value.text = it.WorkOrderNum
            textview_handler_value.text = it.Handler
            textview_deal_time_value.text = it.DelTime
            textview_job_list_content_value.viewTreeObserver?.addOnGlobalLayoutListener {
                    if (textview_job_list_content_value.lineCount > 1) {
                        textview_job_list_content_value.gravity = Gravity.LEFT
                    }

                    textview_job_list_content_value.text = it.Content
                }

            if (!it.FilePath.isNullOrBlank() && !it.FileHead.isNullOrBlank()) {
                val pictureAdapter = PictureAdapter(this)
                val arraylistPicture = ArrayList<String>()
                val strPath = it.FilePath?.split("\\|")

                strPath?.let {
                    for (str in strPath) {
                        arraylistPicture.add(data.FileHead.toString() + str)
                    }

                    pictureAdapter.setData(arraylistPicture)

                    gridrecycleview.setHorizontalSpace(8)
                    gridrecycleview.setVerticalSpace(8)
                    gridrecycleview.adapter = pictureAdapter
                }
            } else {
                textview_image.visibility = View.GONE
                divider6.visibility = View.GONE
                gridrecycleview.visibility = View.GONE
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_job_list_detail

    override fun getTitleId() = R.string.job_list_detail

    companion object{
        fun start(context: Context, data: JobListData.Data?) {
            val starter = Intent(context, JobListDetail::class.java)
            starter.putExtra("data", data)
            context.startActivity(starter)
        }
    }
}