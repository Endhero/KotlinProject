package com.lcd.kotlinproject.vm.maintain

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.source.MaintainRepository
import com.lcd.kotlinproject.data.source.UserRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class MaintainPlanListViewModel(application: Application): BaseViewModel(application) {
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()
    private val mMaintainRepository: MaintainRepository = MaintainRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mTipUtil: TipUtil = TipUtil()

    companion object{
        const val GET_ORGUID_SUCCESS = 18
        const val GET_ORGUID_FAIL = 19
        const val GET_ORGUID_ERROR = 20
        const val GET_MAINTAIN_PLAN_SUCCESS = 21
        const val GET_MAINTAIN_PLAN_FAIL = 22
        const val GET_MAINTAIN_PLAN_ERROR = 23
    }

    private fun setResult(message: Message) = mResult.postValue(message)

    fun getResult(): MutableLiveData<Message> = mResult

    fun getOrgData() {
        mUserRepository.getOrgData()
            .map(Function<Ret<OrgData?>,Ret<String?>> { orgDataRet ->
                val ret: Ret<String?> = Ret.newRet(orgDataRet.success)
                ret.data = orgDataRet.data?.id
                ret.msg = orgDataRet.msg
                ret
            }).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe(object : Observer<Ret<String?>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(ret: Ret<String?>) {
                    val message = Message.obtain()

                    if (ret.success) {
                        message.what = GET_ORGUID_SUCCESS
                        getMainTainPlan(ret.data!!)
                    } else {
                        message.what = GET_ORGUID_FAIL
                        message.obj = mTipUtil.getFailTip(GET_ORGUID_FAIL, ret)
                    }

                    setResult(message)
                }

                override fun onError(throwable: Throwable) {
                    val message = Message.obtain()
                    message.what = GET_ORGUID_ERROR
                    message.obj = mTipUtil.getErrorTip(GET_ORGUID_ERROR, throwable)

                    setResult(message)
                }

                override fun onComplete() {}
            })
    }

    private fun getMainTainPlan(strOrgUID: String) {
        mMaintainRepository.getMainTainPlan(strOrgUID)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe(object : Observer<Ret<List<MaintainData?>>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(ret: Ret<List<MaintainData?>>) {
                    val message = Message.obtain()

                    if (ret.success) {
                        message.what = GET_MAINTAIN_PLAN_SUCCESS
                        message.obj = ret.data
                    } else {
                        message.what = GET_MAINTAIN_PLAN_FAIL
                        message.obj = mTipUtil.getFailTip(
                            GET_MAINTAIN_PLAN_FAIL,
                            ret
                        )
                    }

                    setResult(message)
                }

                override fun onError(throwable: Throwable) {
                    val message = Message.obtain()
                    message.what = GET_MAINTAIN_PLAN_ERROR
                    message.obj = mTipUtil.getErrorTip(GET_MAINTAIN_PLAN_ERROR, throwable)

                    setResult(message)
                }

                override fun onComplete() {}
            })
    }
}