package com.lcd.kotlinproject.view.impl.maintain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.view.adapter.widget.PictureAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.view.widget.GridRecycleView
import org.jetbrains.anko.doAsyncResult
import java.net.URL
import java.util.*

class JobListDetail : ToolbarActivity() {
    @BindView(R.id.divider6)
    lateinit var divider: View
    @BindView(R.id.textview_image)
    lateinit var textviewImage: TextView
    @BindView(R.id.scrollview)
    lateinit var scrollview: ScrollView
    @BindView(R.id.textview_group_value)
    lateinit var textViewGroup: TextView
    @BindView(R.id.textview_job_list_type_value)
    lateinit var textViewType: TextView
    @BindView(R.id.textview_job_list_code_value)
    lateinit var textViewCode: TextView
    @BindView(R.id.textview_handler_value)
    lateinit var textViewHandler: TextView
    @BindView(R.id.textview_deal_time_value)
    lateinit var textViewDealTime: TextView
    @BindView(R.id.textview_job_list_content_value)
    lateinit var textViewContent: TextView
    @BindView(R.id.gridrecycleview)
    lateinit var gridrecycleview: GridRecycleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView(){
        val data = intent.getSerializableExtra("data") as JobListData.Data

        data?.let {
            textViewGroup.text = it.DeviceName
            textViewType.text = it.OrderType
            textViewCode.text = it.WorkOrderNum
            textViewHandler.text = it.Handler
            textViewDealTime.text = it.DelTime
            textViewContent.viewTreeObserver?.addOnGlobalLayoutListener {
                    if (textViewContent.lineCount > 1) {
                        textViewContent.gravity = Gravity.LEFT
                    }

                    textViewContent.text = it.Content
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
                textviewImage.visibility = View.GONE
                divider.visibility = View.GONE
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