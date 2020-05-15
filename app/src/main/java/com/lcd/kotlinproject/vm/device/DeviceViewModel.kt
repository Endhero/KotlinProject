package com.lcd.kotlinproject.vm.device

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.request.ControlCommand
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.data.model.remote.respose.DeviceStatusData
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.data.source.DeviceRepository
import com.lcd.kotlinproject.data.source.UserRepository
import com.lcd.kotlinproject.view.widget.SMSCodeInputDialog
import com.lcd.kotlinproject.view.widget.SendMessageDialog
import com.uber.autodispose.AutoDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DeviceViewModel(application: Application): BaseViewModel(application) {
    private val deviceRepository: DeviceRepository = DeviceRepository(application)
    private val userRepository: UserRepository = UserRepository(application)
    private val deviceLiveData: MutableLiveData<List<DeviceData?>> = MutableLiveData<List<DeviceData?>>()
    private val statusDataLiveData: MutableLiveData<DeviceStatusData> = MutableLiveData<DeviceStatusData>()
    private val orgLiveData = MutableLiveData<String>()
    private var id: String? = null
    private var loginUser: UserData? = null
    private var items: Array<String>? = null

    init {
        addSchedule()
        getLoginUser()
        getOrgData()
    }

    fun getDeviceList(force: Boolean) {
        deviceRepository.getDeviceList(force)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    ret.data?.let {deviceLiveData.postValue(ret.data)}
                }
            }) { _ -> }
    }

    fun getDeviceState(id: String) {
        this.id = id

        deviceRepository.getDeviceStatus(id)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    statusDataLiveData.postValue(ret.data)
                }
            }) { _ -> }
    }

    private fun addSchedule() {
        Observable.interval(2, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe { _ ->
                if (!id.isNullOrBlank()) {
                    getDeviceState(id!!)
                }
            }
    }

    private fun refreshDeviceState(id: String) {
        deviceRepository.forceGetDeviceStatus(id)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    statusDataLiveData.postValue(ret.data)
                }
            }) { _ -> }
    }

    private fun getLoginUser() {
        userRepository.getLoginUser()
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    loginUser = ret.data
                }
            }) { _ -> }
    }

    fun control(code: String, command: String, context: Context) {
        val controlCommand = ControlCommand(code, command, id)
        deviceRepository.sendCtrlCommand(controlCommand)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    SMSCodeInputDialog.getInstance().dismiss()
                    SendMessageDialog(context).show()
                    refreshDeviceState(id!!)
                }
            }) { _ -> SMSCodeInputDialog.clear() }
    }

    fun getDeviceLiveData() = deviceLiveData

    fun getStatusDataLiveData() = statusDataLiveData

    fun getOrgLiveData() = orgLiveData


    fun getUserMobile()= loginUser?.mobile

    private fun getOrgData() {
        userRepository.getOrgData()
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret -> orgLiveData.setValue(ret.data!!.id) }) { _ -> }
    }

    fun getDeviceNameArray(): Array<String>? {
        if (items.isNullOrEmpty()) {
            val list = deviceLiveData.value

            if (list != null) {
                items = Array<String>(list.size) { it -> it.toString() }

                for (i in list.indices) {
                    items!![i] = list[i]?.name.toString()
                }
            }
        }

        return items
    }

    fun getDeviceId(index: Int) = deviceLiveData.value?.get(index)?.id
}