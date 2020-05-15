package com.lcd.kotlinproject.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.LoginData
import com.lcd.kotlinproject.data.source.AccountRepository
import com.uber.autodispose.AutoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Chen on 2019/12/5.
 */
class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val accountRepository: AccountRepository = AccountRepository(application)
    private val sendCodeRetLiveData: MutableLiveData<Ret<*>> = MutableLiveData<Ret<*>>()
    private val loginRetLiveData: MutableLiveData<Ret<*>> = MutableLiveData<Ret<*>>()

    fun sendCode(phone: String, type: Int) {
        accountRepository.sendCode(phone, type)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ res ->
//                    if (type == 0) {
                sendCodeRetLiveData.postValue(res)
            }, { _ -> sendCodeRetLiveData.postValue(Ret.newRet<String?>(false)) }, {}
                , { _ -> })
    }

    fun login(phone: String, code: String) {
        accountRepository.login(phone, code)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ res -> loginRetLiveData.postValue(res) }, { _ -> }, {}
                , { _ -> })
    }

    private fun autoLogin() {
        accountRepository.autoLogin()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret -> loginRetLiveData.postValue(ret) },
                { _ -> loginRetLiveData.postValue(Ret.newRet<LoginData?>(false)) },
                {},
                { _ -> })
    }

    fun canAutoLogin(): Boolean {
        return if (accountRepository.canAutoLogin()) {
            autoLogin()
            true
        } else {
            false
        }
    }

    fun getSendCodeRetLiveData() = sendCodeRetLiveData

    fun getLoginRetLiveData() = loginRetLiveData
}