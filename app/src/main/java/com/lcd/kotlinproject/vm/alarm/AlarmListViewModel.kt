package com.lcd.kotlinproject.vm.alarm

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.request.AlarmSearchRequest
import com.lcd.kotlinproject.data.model.remote.respose.AlarmListData
import com.lcd.kotlinproject.data.source.AlarmRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AlarmListViewModel(application: Application) : BaseViewModel(application){
    private var mAlarmRepository: AlarmRepository = AlarmRepository(application)
    private val mTipUtil: TipUtil = TipUtil()
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()

    fun getResult() =  mResult

    private fun setResult(message: Message) {
        mResult.postValue(message)
    }

    companion object {
        const val NOCLEAR = 0
        const val CLEAR = 1
        const val SEARCH_ALARM_SUCCESS = 10
        const val SEARCH_ALARM_FAIL = 11
        const val SEARCH_ALARM_ERROR = 12
    }

    fun searchAlarm(strDate: String, strDeviceUID: String, nExceptionGrade: Int, nPage: Int, nPageSize: Int) {
        val alarmsearchrequest = AlarmSearchRequest(strDate, strDeviceUID, nExceptionGrade, nPage, nPageSize)

        mAlarmRepository.getAlarmList(alarmsearchrequest).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe(object : Observer<Ret<AlarmListData?>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(ret: Ret<AlarmListData?>) {
                    val message = Message.obtain()

                    if (ret.success) {
                        message.what = SEARCH_ALARM_SUCCESS
                        message.obj = ret.data
                    } else {
                        message.what = SEARCH_ALARM_FAIL
                        message.obj = mTipUtil.getFailTip(SEARCH_ALARM_FAIL, ret)
                    }

                    setResult(message)
                }

                override fun onError(throwable: Throwable) {
                    val message = Message.obtain()
                    message.what = SEARCH_ALARM_ERROR
                    message.obj = mTipUtil.getErrorTip(SEARCH_ALARM_ERROR, throwable)

                    setResult(message)
                }

                override fun onComplete() {}
            })
    }
}