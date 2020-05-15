package com.lcd.kotlinproject.vm.rog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lcd.base.remote.Ret
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.data.source.UserRepository
import com.uber.autodispose.AutoDispose
import io.reactivex.schedulers.Schedulers

class ContactEditViewModel(application: Application): BaseViewModel(application) {
    private val userRepository: UserRepository = UserRepository(application)
    private val retMutableLiveData: MutableLiveData<Ret<Void>> = MutableLiveData<Ret<Void>>()

    fun add(user: UserData) {
        userRepository.addUser(user)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret -> retMutableLiveData.postValue(ret) }) { _ -> }
    }

    fun edit(user: UserData) {
        userRepository.updateUser(user)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret -> retMutableLiveData.postValue(ret) }) { _ -> }
    }

    fun delete(id: String) {
        userRepository.deleteUser(id!!)
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret -> retMutableLiveData.postValue(ret) }) { _ -> }
    }

    fun getRetMutableLiveData() = retMutableLiveData
}