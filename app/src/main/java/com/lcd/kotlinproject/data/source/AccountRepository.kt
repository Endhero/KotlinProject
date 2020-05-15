package com.lcd.kotlinproject.data.source

import android.content.Context
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.LoginRequest
import com.lcd.kotlinproject.data.model.remote.request.RefreshRequest
import com.lcd.kotlinproject.data.model.remote.request.SendCodeRequest
import com.lcd.kotlinproject.data.model.remote.respose.LoginData
import com.lcd.kotlinproject.data.source.local.MemorySource
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.AccountAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator
import io.reactivex.Observable

class AccountRepository (context: Context){
    private var accountAPI: AccountAPI = ServiceGenerator.createService(AccountAPI::class.java)
    private var preferenceSource: PreferenceSource = PreferenceSource()
    private var memorySource: MemorySource = MemorySource()

    fun login(phone: String, code: String): Observable<Ret<LoginData?>> {
        val request = LoginRequest(phone, code, preferenceSource.getAppId())

        return accountAPI.login(request)
            .map { ret ->
                if (ret.success) {
                    var loginData: LoginData = ret.data as LoginData
                    preferenceSource.setLoginInfo(
                        loginData.accessToken!!,
                        loginData.refreshToken!!,
                        loginData.expires
                    )
                    preferenceSource.setLoginUser(phone)
                    memorySource.put("token", loginData.accessToken!!)
                }

                ret
            }
    }

//            return accountAPI.login(request)
//            .map(object : io.reactivex.functions.Function<Ret<LoginData?>, Ret<LoginData?>>{
//                override fun apply(ret: Ret<LoginData?>): Ret<LoginData?> {
//                    if (ret.success){
//                        var loginData: LoginData = ret.data as LoginData
//                        preferenceSource.setLoginInfo(loginData.accessToken!!, loginData.refreshToken!!, loginData.expires)
//                        preferenceSource.setLoginUser(phone)
//                        memorySource.put("token", loginData.accessToken!!)
//                    }
//
//                    return ret
//                }
//            })

    fun autoLogin(): Observable<Ret<Void>> {
        if (preferenceSource.getToken().isNullOrBlank()) {
            return Observable.just(Ret.newRet(false))
        }

        return if (preferenceSource.getExpireTime()!!.time < System.currentTimeMillis()) {
            refresh().map{
                    logindata->

                    val ret: Ret<Void> = Ret.newRet(logindata.success)
                    ret.msg = logindata.msg
                    ret.code = logindata.code

                    ret
            }
        } else Observable.just(Ret.newRet(true))
    }

    fun sendCode(phone: String, type: Int): Observable<Ret<String?>> {
        val request = SendCodeRequest(phone, preferenceSource.getAppId(), type)
        return accountAPI.sendAuthCode(request)
    }

    fun clearLoginInfo() {
        memorySource.clear()
        preferenceSource.setLoginInfo("", "", null)
        preferenceSource.setLoginUser(null)
        //        MobclickAgent.onProfileSignOff();
    }

    fun canAutoLogin() = !preferenceSource.getLoginUser().isNullOrEmpty()

    fun refresh(): Observable<Ret<LoginData?>> {
        return accountAPI.refresh(RefreshRequest(preferenceSource.getAppId(), preferenceSource.getRefreshToken()))
            .map{ loginDataRet ->
                if (loginDataRet.success) {
                    val data: LoginData = loginDataRet.data!!
                    preferenceSource.setLoginInfo(data.accessToken!!, data.refreshToken!!, data.expires);
                    }

            loginDataRet
            }
    }

    fun getLoginUser(): String? = preferenceSource.getLoginUser()

    fun getAuthorization() = "Bearer " + getToken()

    fun getToken(): String? {
        var token: String? = memorySource["token"] as String?

        if (token.isNullOrBlank()) {
            token = preferenceSource.getToken()
            memorySource.put("token", token!!)
        }

        return token
    }

    fun setToken(token: String?) {
        memorySource.put("token", token!!)
        preferenceSource.setToken(token)
    }
}