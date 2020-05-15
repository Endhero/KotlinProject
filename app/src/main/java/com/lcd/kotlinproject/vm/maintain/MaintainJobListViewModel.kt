package com.lcd.kotlinproject.vm.maintain

import android.app.Application
import android.os.Message
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.source.MaintainRepository
import com.lcd.kotlinproject.data.source.UserRepository
import com.lcd.kotlinproject.utils.TipUtil
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_SUCCESS
import com.uber.autodispose.AutoDispose
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class MaintainJobListViewModel(application: Application): BaseViewModel(application) {
    private val mMaintainRepository: MaintainRepository = MaintainRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mTipUtil: TipUtil = TipUtil()
    private val mResult: MutableLiveData<Message> = MutableLiveData<Message>()
    private var mOrgUID: String? = null

    companion object{
        const val GET_MAINTAIN_JOB_LIST_SUCCESS = 27
        const val GET_MAINTAIN_JOB_LIST_FAIL = 28
        const val GET_MAINTAIN_JOB_LIST_ERROR = 29
    }

    private fun setResult(message: Message)= mResult.postValue(message)

    fun getResult(): MutableLiveData<Message> =  mResult

    fun getOrgData() {
        mUserRepository.getOrgData()
            .map(Function<Ret<OrgData?>, Ret<String?>> { orgDataRet ->
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
                        mOrgUID = ret.data
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

    fun getWorkOrder(nPageNum: Int) {
        if (!mOrgUID.isNullOrBlank()) {
            mMaintainRepository.getWorkOrder(mOrgUID!!, nPageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .`as`(AutoDispose.autoDisposable(this))
                .subscribe(object : Observer<Ret<JobListData?>> {
                    override fun onSubscribe(disposable: Disposable) {}
                    override fun onNext(ret: Ret<JobListData?>) {
                        val message = Message.obtain()

                        if (ret.success) {
                            message.what = GET_MAINTAIN_JOB_LIST_SUCCESS
                            message.obj = ret.data
                        } else {
                            message.what = GET_MAINTAIN_JOB_LIST_FAIL
                            message.obj = mTipUtil.getFailTip(GET_MAINTAIN_JOB_LIST_FAIL, ret)
                        }

                        setResult(message)
                    }

                    override fun onError(throwable: Throwable) {
                        val message = Message.obtain()
                        message.what = GET_MAINTAIN_JOB_LIST_ERROR
                        message.obj = mTipUtil.getErrorTip(
                            GET_MAINTAIN_JOB_LIST_ERROR,
                            throwable
                        )
                        setResult(message)
                    }

                    override fun onComplete() {}
                })
        }
    }
}