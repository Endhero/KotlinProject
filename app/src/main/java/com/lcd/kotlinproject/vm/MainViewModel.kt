package com.lcd.kotlinproject.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.request.NoClearSaerchRequest
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.source.AccountRepository
import com.lcd.kotlinproject.data.source.AlarmRepository
import com.lcd.kotlinproject.data.source.UserRepository
import com.uber.autodispose.AutoDispose
import io.reactivex.schedulers.Schedulers

/**
 * Created by Chen on 2020/3/18.
 */
class MainViewModel(application: Application) : BaseViewModel(application) {
    private val userRepository: UserRepository = UserRepository(application)
    private val accountRepository: AccountRepository = AccountRepository(application)
    private val alarmRepository: AlarmRepository = AlarmRepository(application)
    private val orgDataLiveData: MutableLiveData<OrgData> = MutableLiveData<OrgData>()
    private val unreadAlarmLiveData = MutableLiveData<Int>()

    fun getOrgData() {
        userRepository.getOrgData()
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.single())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ orgDataRet ->
                if (orgDataRet.success) {
                    orgDataLiveData.postValue(orgDataRet.data)
                }
            }, { _ -> })
        }

    fun getUnreadCount() {
        val request = NoClearSaerchRequest()
        alarmRepository.getNoClearQuantity(request)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.single())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    val count = ret.data!!.NoClearNum
                    unreadAlarmLiveData.postValue(count)
                }
            }, { _ -> })
        }

    fun getOrgDataLiveData() = orgDataLiveData

    fun getUnreadAlarmLiveData() = unreadAlarmLiveData

    fun logoff()= accountRepository.clearLoginInfo()
}