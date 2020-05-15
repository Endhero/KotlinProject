package com.lcd.kotlinproject.vm.camera

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.ezvizuikit.open.EZUIError
import com.ezvizuikit.open.EZUIPlayer
import com.ezvizuikit.open.EZUIPlayer.EZUIPlayerCallBack
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.Const
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.*
import com.lcd.kotlinproject.view.widget.VideoView
import com.uber.autodispose.AutoDispose
import com.videogo.widget.CheckTextButton
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.longToast
import java.util.*

class CameraViewModel(application: Application): BaseViewModel(application), EZUIPlayerCallBack, LifecycleObserver {
    private var mActivity: AppCompatActivity? = null
    private var mVideoView: VideoView? = null
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()
    private var mScreenOrientationHelper: ScreenOrientationHelper? = null
    private val mTipUtil: TipUtil = TipUtil()

    companion object{
        const val GET_DEVICEINFO_SUCCESS = 13
        const val GET_DEVICEINFO_ERROR = 14
        const val VIDEO_PLAY_FAIL = 15
    }

    private fun setResult(message: Message) {
        mResult.postValue(message)
    }

    fun getResult(): MutableLiveData<Message> = mResult

    fun bindVideoView(activity: AppCompatActivity, videoview: VideoView) {
        mActivity = activity
        mActivity?.lifecycle?.addObserver(this)
        mVideoView = videoview
        mVideoView?.setCallBack(this)
    }

    fun bindFullScreenBotton(activity: Activity, checktextbutton: CheckTextButton?) {
        mScreenOrientationHelper = ScreenOrientationHelper(activity, checktextbutton)
    }

    fun capturePicture(strFileName: String) {
        val strFilePath: String =Const.CAPTRUE_SAVE_PATH.toString() + "/" + strFileName + ".jpg"

        if (PermissionUtil.applyPermissionStorage(mActivity!!)) {
            val bitmap = mVideoView?.capturePicture()

            if (bitmap != null) {
                if (PhotoUtil.savePhotoToGallery(strFilePath, bitmap) === PhotoUtil.PHOTO_SAVE_SUCCESS) {
                    DialogUtil.showConfirmDialog(
                        mActivity,
                        R.string.captrue_success,
                        mActivity!!.getString(R.string.captrue_success_format, strFilePath)
                    )
                    return
                }
            }

            mActivity!!.longToast(mActivity!!.getString(R.string.captrue_fail))
        }
    }

    fun startTalk() = if (PermissionUtil.applyPermissionAudio(mActivity!!)) {
            mVideoView?.startTalk()

            true
        } else {
            false
        }

    fun getDeviceInfo(strDeviceSerial: String?) {
        if (strDeviceSerial.isNullOrBlank()) {
            return
        }

        CameraUtil.getDeviceInfo(strDeviceSerial)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ deviceinfo ->
                val message = Message.obtain()

                message.what = GET_DEVICEINFO_SUCCESS
                message.obj = deviceinfo

                setResult(message)
            }, { throwable ->
                val message = Message.obtain()

                message.what = GET_DEVICEINFO_ERROR
                message.obj = mTipUtil.getErrorTip(GET_DEVICEINFO_ERROR, throwable)

                setResult(message)
            })
    }

    fun changeOrientation(nOrientation: Int) {
        when(nOrientation){
            Configuration.ORIENTATION_LANDSCAPE-> mScreenOrientationHelper?.landscape()
            Configuration.ORIENTATION_PORTRAIT-> mScreenOrientationHelper?.portrait()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mScreenOrientationHelper?.enableSensorOrientation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mScreenOrientationHelper?.disableSensorOrientation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (mVideoView!!.status === EZUIPlayer.STATUS_PLAY) {
            mVideoView?.stopPlay()
        }

        mScreenOrientationHelper?.postOnStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mVideoView?.releasePlayer()
        mScreenOrientationHelper = null
    }

    override fun onPlayTime(calendar: Calendar?) {
    }

    override fun onPrepared() {
    }

    override fun onVideoSizeChange(nWidth: Int, nHeight: Int) {
    }

    override fun onPlayFail(ezuierror: EZUIError) {
        val message = Message.obtain()

        message.what = VIDEO_PLAY_FAIL
        message.obj = mTipUtil.getFailTip(VIDEO_PLAY_FAIL, ezuierror.internalErrorCode.toString() + ":" + ezuierror.errorString)
    }

    override fun onPlaySuccess() {
    }

    override fun onPlayFinish() {
    }

    fun startMoveUp(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.startMoveUp(strDeviceSerial, nCameraNo)

    fun startMoveDown(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.startMoveDown(strDeviceSerial, nCameraNo)

    fun startMoveLeft(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.startMoveLeft(strDeviceSerial, nCameraNo)

    fun startMoveRight(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.startMoveRight(strDeviceSerial, nCameraNo)

    fun stopMoveUp(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.stopMoveUp(strDeviceSerial, nCameraNo)

    fun stopMoveDown(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.stopMoveDown(strDeviceSerial, nCameraNo)

    fun stopMoveLeft(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.stopMoveLeft(strDeviceSerial, nCameraNo)

    fun stopMoveRight(strDeviceSerial: String, nCameraNo: Int) = CameraUtil.stopMoveRight(strDeviceSerial, nCameraNo)
}