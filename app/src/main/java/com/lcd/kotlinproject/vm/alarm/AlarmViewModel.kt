package com.lcd.kotlinproject.vm.alarm

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.local.DeviceInfo
import com.lcd.kotlinproject.data.model.remote.request.NoClearSaerchRequest
import com.lcd.kotlinproject.data.model.remote.respose.DeviceStatusData
import com.lcd.kotlinproject.data.model.remote.respose.NoClearData
import com.lcd.kotlinproject.data.source.AlarmRepository
import com.lcd.kotlinproject.data.source.DeviceRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*

class AlarmViewModel(application: Application) : BaseViewModel(application) {
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()
    private val mAlarmRepository: AlarmRepository = AlarmRepository(application)
    private val mDeviceRepository: DeviceRepository = DeviceRepository(application)
    private val mTipUtil: TipUtil = TipUtil()

    companion object {
        const val GET_DEVICE_LIST_SUCCESS = 4
        const val GET_DEVICE_LIST_FAIL = 5
        const val GET_DEVICE_LIST_ERROR = 6
        const val GET_NOCLEAR_QUANTITY_SUCCESS = 7
        const val GET_NOCLEAR_QUANTITY_FAIL = 8
        const val GET_NOCLEAR_QUANTITY_ERROR = 9
    }

    fun getResult() = mResult

    private fun setResult(message: Message) = mResult.postValue(message)

    fun getDeviceList() = mDeviceRepository.getDeviceStatusList()
        .map(Function<Ret<List<DeviceStatusData?>>, Ret<List<DeviceInfo?>>> { ret ->
            val listDeviceInfo: MutableList<DeviceInfo> = ArrayList<DeviceInfo>()
            if (ret.data != null) {
                for (devicestatusdata in ret.data) {
                    devicestatusdata?.let {
                        val deviceinfo = DeviceInfo(devicestatusdata.name, devicestatusdata.uid)
                        listDeviceInfo.add(deviceinfo)
                    }
                }
            }
            val retDeviceInfo: Ret<List<DeviceInfo?>> = Ret.newRet(ret.success)
            retDeviceInfo.code = ret.code
            retDeviceInfo.msg = ret.msg
            retDeviceInfo.data = listDeviceInfo

            retDeviceInfo
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .`as`(AutoDispose.autoDisposable(this))
        .subscribe(object : Observer<Ret<List<DeviceInfo?>>> {
            override fun onSubscribe(disposable: Disposable) {}
            override fun onNext(ret: Ret<List<DeviceInfo?>>) {
                val message = Message.obtain()

                if (ret.success) {
                    message.what = GET_DEVICE_LIST_SUCCESS
                    message.obj = ret.data
                } else {
                    message.what = GET_DEVICE_LIST_FAIL
                    message.obj = mTipUtil.getFailTip(GET_DEVICE_LIST_FAIL, ret)
                }

                setResult(message)
            }

            override fun onError(throwable: Throwable) {
                val message = Message.obtain()
                message.what = GET_DEVICE_LIST_ERROR
                message.obj = mTipUtil.getErrorTip(GET_DEVICE_LIST_ERROR, throwable)
                setResult(message)
            }

            override fun onComplete() {}
        })

    fun getNoReadQuantity(strStartDate: String?, strDeviceUID: String?) = mAlarmRepository.getNoClearQuantity(NoClearSaerchRequest(strStartDate, strDeviceUID)).subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .`as`(AutoDispose.autoDisposable(this))
        .subscribe(object : Observer<Ret<NoClearData?>> {
            override fun onSubscribe(disposable: Disposable) {}
            override fun onNext(ret: Ret<NoClearData?>) {
                val message = Message.obtain()

                if (ret.success) {
                    message.what = GET_NOCLEAR_QUANTITY_SUCCESS
                    message.obj = ret.data
                } else {
                    message.what = GET_NOCLEAR_QUANTITY_FAIL
                }
                setResult(message)
            }

            override fun onError(throwable: Throwable) {
                val message = Message.obtain()
                message.what = GET_NOCLEAR_QUANTITY_ERROR
                setResult(message)
            }

            override fun onComplete() {}
        })
}