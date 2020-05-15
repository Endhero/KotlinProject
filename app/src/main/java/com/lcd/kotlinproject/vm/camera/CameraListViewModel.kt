package com.lcd.kotlinproject.vm.camera

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.CameraListData
import com.lcd.kotlinproject.data.source.CameraRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CameraListViewModel(application: Application): BaseViewModel(application) {
    private val mCameraRepository: CameraRepository = CameraRepository(application)
    private val mResult: MutableLiveData<Message> = MutableLiveData<android.os.Message>()
    private val mTipUtil: TipUtil = TipUtil()

    companion object{
        const val GET_CAMERA_LIST_SUCCESS = 1
        const val GET_CAMERA_LIST_FAIL = 2
        const val GET_CAMERA_LIST_ERROR = 3
    }

    fun getResult(): MutableLiveData<Message> = mResult

    private fun setResult(message: Message) {
        mResult.postValue(message)
    }

    fun getCameraList() {
        mCameraRepository.getCameraList()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe(object : Observer<Ret<CameraListData?>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(ret: Ret<CameraListData?>) {
                    val message = Message.obtain()

                    if (ret.success) {
                        message.what = GET_CAMERA_LIST_SUCCESS
                        message.obj = ret.data
                    } else {
                        message.what = GET_CAMERA_LIST_FAIL
                        message.obj = mTipUtil.getFailTip(GET_CAMERA_LIST_FAIL, ret)
                    }

                    setResult(message)
                }

                override fun onError(throwable: Throwable) {
                    val message = Message.obtain()

                    message.what = GET_CAMERA_LIST_ERROR
                    message.obj = mTipUtil.getErrorTip(GET_CAMERA_LIST_ERROR, throwable)

                    setResult(message)
                }

                override fun onComplete() {}
            })
    }
}