package com.lcd.kotlinproject.vm.maintain

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.data.model.remote.respose.MaintainRecordData
import com.lcd.kotlinproject.data.source.MaintainRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MaintainRecordViewModel(application: Application): BaseViewModel(application) {
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()
    private val mMaintainRepository: MaintainRepository = MaintainRepository(application)
    private val mTipUtil: TipUtil = TipUtil()
    private fun setResult(message: Message) = mResult.postValue(message)

    fun getResult(): MutableLiveData<Message> = mResult

    fun getMaintainRecord(maintaindata: MaintainData, nPageNum: Int) {
        mMaintainRepository.getMainTainInfo(maintaindata.OrgUID!!, nPageNum, maintaindata.DeviceUID!!, maintaindata.EquipmentType!!.toInt())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe(object : Observer<Ret<MaintainRecordData?>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(ret: Ret<MaintainRecordData?>) {
                    val message = Message.obtain()

                    if (ret.success) {
                        message.what = GET_MAINTAIN_RECORD_SUCCESS
                        message.obj = ret.data
                    } else {
                        message.what = GET_MAINTAIN_RECORD_FAIL
                        message.obj = mTipUtil.getFailTip(GET_MAINTAIN_RECORD_FAIL, ret)
                    }

                    setResult(message)
                }

                override fun onError(throwable: Throwable) {
                    val message = Message.obtain()

                    message.what = GET_MAINTAIN_RECORD_ERROR
                    message.obj = mTipUtil.getErrorTip(GET_MAINTAIN_RECORD_ERROR, throwable)

                    setResult(message)
                }

                override fun onComplete() {}
            })
    }

    companion object{
        const val GET_MAINTAIN_RECORD_SUCCESS = 24
        const val GET_MAINTAIN_RECORD_FAIL = 25
        const val GET_MAINTAIN_RECORD_ERROR = 26
    }
}