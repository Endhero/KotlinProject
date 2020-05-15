package com.lcd.kotlinproject.view.impl.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezvizuikit.open.EZUIKit
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.CameraData
import com.lcd.kotlinproject.data.model.remote.respose.CameraListData
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.view.adapter.camera.CameraListAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.vm.camera.CameraListViewModel
import com.lcd.kotlinproject.vm.camera.CameraListViewModel.Companion.GET_CAMERA_LIST_ERROR
import com.lcd.kotlinproject.vm.camera.CameraListViewModel.Companion.GET_CAMERA_LIST_FAIL
import com.lcd.kotlinproject.vm.camera.CameraListViewModel.Companion.GET_CAMERA_LIST_SUCCESS
import com.videogo.openapi.EZOpenSDK
import com.yuwell.androidbase.tool.ResourceUtil
import kotlinx.android.synthetic.main.activity_camera_list.*

class CameraList : ToolbarActivity() {
    private var mCameraListViewModel: CameraListViewModel? = null
    private var mCameraListAdapter: CameraListAdapter = CameraListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initViewModel()

        mCameraListViewModel!!.getCameraList()
    }

    private fun initView(){
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = mCameraListAdapter

        mCameraListAdapter.setOnItemClickListener(object : CameraListAdapter.OnItemClickListener {
            override fun onItemClick(nPosition: Int, cameradata: CameraData?, bIsOnline: Boolean) {
                if (bIsOnline) {
                    Camera.start(this@CameraList, cameradata)
                }
            }
        })

        showProgressDialog(R.string.please_wait)
    }

    private fun initViewModel(){
        mCameraListViewModel = ViewModelProviders.of(this).get(CameraListViewModel::class.java)
        mCameraListViewModel!!.getResult()
            .observe(this, Observer<Message> {message->
                dismissProgressDialog()

                when (message.what) {
                    GET_CAMERA_LIST_SUCCESS -> {
                        val cameralistdata = message.obj as CameraListData

                        if (!cameralistdata.CameraList.isNullOrEmpty()) {
                            EZOpenSDK.getInstance().setAccessToken(cameralistdata.AccessToken)
                            EZUIKit.setAccessToken(cameralistdata.AccessToken)
                            mCameraListAdapter.setData(cameralistdata.CameraList)
                        } else {
                            textview_no_data.visibility = View.VISIBLE
                        }
                    }
                    GET_CAMERA_LIST_FAIL, GET_CAMERA_LIST_ERROR -> {
                        textview_no_data.visibility = View.VISIBLE
                        DialogUtil.showConfirmDialog(this, R.string.error, message.obj as String)
                    }
                }
            })

        mCameraListViewModel!!.globalErrorLiveData.observe(this, Observer{ error ->
            dismissProgressDialog()
            showMessage(ResourceUtil.getStringId(application, "error_" + error.type))
        })
    }

    override fun getLayoutId() = R.layout.activity_camera_list

    override fun getTitleId() = R.string.monitor

    override fun onDestroy() {
        super.onDestroy()
        EZOpenSDK.getInstance().logout()
    }

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, CameraList::class.java)
            context.startActivity(starter)
        }
    }
}