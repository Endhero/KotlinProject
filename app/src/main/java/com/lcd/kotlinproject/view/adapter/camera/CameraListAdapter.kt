package com.lcd.kotlinproject.view.adapter.camera

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.CameraData
import com.lcd.kotlinproject.utils.CameraUtil
import com.lcd.kotlinproject.utils.CameraUtil.DEVICE_OFFLINE
import com.lcd.kotlinproject.utils.CameraUtil.DEVICE_ONLINE
import com.lcd.kotlinproject.view.adapter.base.BaseListAdapter
import com.lcd.kotlinproject.view.viewholder.CameraViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CameraListAdapter(val context: Context) : BaseListAdapter<CameraViewHolder, CameraData> (){
    private var mData: MutableList<CameraData>? = null
    private var mOnItemClickeListener: OnItemClickListener? = null

    override fun setData(list: MutableList<CameraData>?) {
        mData = list
        notifyDataSetChanged()
    }

    override fun clearData() {
        mData?.clear().let {notifyDataSetChanged()}
    }

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): CameraViewHolder {
        val inflater = LayoutInflater.from(viewgroup.context)

        return CameraViewHolder(
            inflater.inflate(R.layout.item_camera, viewgroup, false)
        )
    }

    override fun getItemCount() = mData?.size ?: 0

    override fun onBindViewHolder(cameraviewholder: CameraViewHolder, nPosition: Int) {
        val cameradata = mData!![nPosition]

        cameraviewholder.imageview.setImageDrawable(null)
        cameraviewholder.textviewName.text = null
        cameraviewholder.textviewErrorTip.visibility = View.GONE
        cameraviewholder.textviewState.setCompoundDrawables(null, null, null, null)
        cameraviewholder.textviewState.text = null

        var drawable: Drawable

        if (cameradata.IsOnline === DEVICE_ONLINE) {
            CameraUtil.captureCamera(cameradata.CameraID, cameradata.ChannelNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ strUrl ->
                    if (!strUrl.isNullOrBlank()) {
                        Glide.with(context).load(strUrl).into(cameraviewholder.imageview)
                    } else {
                        cameraviewholder.imageview.setImageDrawable(null)
                        val drawableError: Drawable = context.getDrawable(R.drawable.ic_load_error)!!
                        drawableError.setBounds(0, 0, drawableError.intrinsicWidth, drawableError.intrinsicHeight)

                        cameraviewholder.textviewErrorTip.visibility = View.VISIBLE
                        cameraviewholder.textviewErrorTip.setCompoundDrawables(null, drawableError, null, null)
                        cameraviewholder.textviewErrorTip.setText(R.string.load_error)
                    }
                }, {
                    cameraviewholder.imageview.setImageDrawable(null)

                    val drawableError: Drawable = context.getDrawable(R.drawable.ic_load_error)!!
                    drawableError.setBounds(0, 0, drawableError.intrinsicWidth, drawableError.intrinsicHeight)

                    cameraviewholder.textviewErrorTip.visibility = View.VISIBLE
                    cameraviewholder.textviewErrorTip.setCompoundDrawables(null, drawableError, null, null)
                    cameraviewholder.textviewErrorTip.setText(R.string.load_error)
                })

            drawable = context.getDrawable(R.drawable.ic_online)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            cameraviewholder.imageview.setImageResource(R.drawable.progress)
            cameraviewholder.textviewErrorTip.visibility = View.GONE
            cameraviewholder.textviewState.setCompoundDrawables(drawable, null, null, null)
            cameraviewholder.textviewState.setTextColor(context.resources.getColor(R.color.blue))
            cameraviewholder.textviewState.setText(R.string.device_online)
        } else if (cameradata.IsOnline === DEVICE_OFFLINE) {
            drawable = context.getDrawable(R.drawable.ic_offline)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            cameraviewholder.textviewState.setCompoundDrawables(drawable, null, null, null)
            cameraviewholder.textviewState.setTextColor(context.resources.getColor(R.color.tipText))
            cameraviewholder.textviewState.setText(R.string.device_offline)

            drawable = context.getDrawable(R.drawable.ic_device_off_line)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            cameraviewholder.textviewErrorTip.setCompoundDrawables(null, drawable, null, null)
            cameraviewholder.textviewErrorTip.visibility = View.VISIBLE
            cameraviewholder.textviewErrorTip.setText(R.string.device_offline)
        }

        if (!cameradata.CameraName.isNullOrEmpty()) {
            cameraviewholder.textviewName.text = cameradata.CameraName
        } else {
            CameraUtil.getDeviceInfo(cameradata.CameraID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ deviceinfo ->
                    if (!deviceinfo?.getDeviceName().isNullOrEmpty()) {
                        cameraviewholder.textviewName.text = deviceinfo?.getDeviceName()
                    }
                }

            cameraviewholder.textviewName.setText(R.string.unkonwn_device)
        }

        cameraviewholder.imageview.setOnClickListener(View.OnClickListener {
            mOnItemClickeListener?.onItemClick( nPosition, cameradata, cameradata.IsOnline === DEVICE_ONLINE)
        })
    }

    fun setOnItemClickListener(onitemclickelistener: OnItemClickListener){
        onitemclickelistener?.let {
            mOnItemClickeListener = it
        }
    }

    interface OnItemClickListener {
        fun onItemClick(nPosition: Int, cameradata: CameraData?, bIsOnline: Boolean)
    }
}