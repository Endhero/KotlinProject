package com.lcd.kotlinproject.data.source

import android.content.Context
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.data.source.local.MemorySource
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.UserAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator
import io.reactivex.Observable

class UserRepository (context: Context){
    private val memorySource: MemorySource = MemorySource()
    private val preferenceSource: PreferenceSource = PreferenceSource()
    private val userAPI: UserAPI = ServiceGenerator.createService(UserAPI::class.java)

    fun getOrgData(): Observable<Ret<OrgData?>>{
        val orgdata : OrgData? = memorySource["org"] as OrgData?

        return if (orgdata != null){
            val ret: Ret<OrgData?> = Ret.newRet(true)
            ret.data = orgdata
            Observable.just(ret)
        } else {
            userAPI.getOrgData(preferenceSource.getAuthorization())
                .map{ret->

                    if (ret.success){
                        ret.data?.let {memorySource.put("org", ret.data!!)}
                    }

                    ret
                }
        }
    }

    fun getUserList(): Observable<Ret<List<UserData?>>> = userAPI.getUserList(preferenceSource.getAuthorization())

    fun addUser(user: UserData): Observable<Ret<Void>> = userAPI.addUser(preferenceSource.getAuthorization(), user)

    fun updateUser(user: UserData): Observable<Ret<Void>> = userAPI.updateUser(preferenceSource.getAuthorization(), user)

    fun deleteUser(id: String): Observable<Ret<Void>> = userAPI.deleteUser(preferenceSource.getAuthorization(), id)

    fun getLoginUser(): Observable<Ret<UserData?>>{
        val loginUser: UserData? = memorySource["user"] as UserData?

        loginUser?.let {
            val retData: Ret<UserData?> = Ret.newRet(true)
            retData.data = loginUser

            return Observable.just(retData)
        }

        return userAPI.getLoginUser(preferenceSource.getAuthorization())
            .map {ret->

                if (ret.success){
                    ret.data?.let {memorySource.put("user", ret.data!!)}
                }

                ret
            }
    }
}