package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.LoginRequest
import com.lcd.kotlinproject.data.model.remote.request.RefreshRequest
import com.lcd.kotlinproject.data.model.remote.request.SendCodeRequest
import com.lcd.kotlinproject.data.model.remote.respose.LoginData
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountAPI {
    @POST("api/Account/Login")
    fun login(@Body request: LoginRequest): Observable<Ret<LoginData?>>

    @POST("api/Account/SendVerifyCode")
    fun sendAuthCode(@Body request: SendCodeRequest): Observable<Ret<String?>>

    @POST("api/Account/RefreshToken")
    fun refresh(@Body request: RefreshRequest): Observable<Ret<LoginData?>>

    @POST("api/Account/RefreshToken")
    fun refreshCallable(@Body request: RefreshRequest): Call<Ret<LoginData?>>
}