package com.lcd.kotlinproject.vm.rog

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lcd.base.vm.BaseViewModel
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.data.source.UserRepository
import com.uber.autodispose.AutoDispose
import io.reactivex.schedulers.Schedulers

class OrgViewModel(application: Application): BaseViewModel(application) {
    private val userRepository: UserRepository = UserRepository(application)
    private val orgDataLiveData: MutableLiveData<OrgData> = MutableLiveData<OrgData>()
    private val usersMutableLiveData: MutableLiveData<List<UserData?>> = MutableLiveData<List<UserData?>>()

    fun getOrgInfo() {
        userRepository.getOrgData()
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    orgDataLiveData.postValue(ret.data)
                }
            }) { _ -> }
    }

    fun getUserList() {
        userRepository.getUserList()
            .compose(globalErrorTransformer())
            .subscribeOn(Schedulers.newThread())
            .`as`(AutoDispose.autoDisposable(this))
            .subscribe({ ret ->
                if (ret.success) {
                    usersMutableLiveData.postValue(ret.data)
                }
            }) { _ -> }
    }

    fun getOrgDataLiveData()= orgDataLiveData

    fun getUsersMutableLiveData() = usersMutableLiveData
}