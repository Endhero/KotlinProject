package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.AlarmSearchRequest
import com.lcd.kotlinproject.data.model.remote.request.NoClearSaerchRequest
import com.lcd.kotlinproject.data.model.remote.respose.AlarmListData
import com.lcd.kotlinproject.data.model.remote.respose.NoClearData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AlarmAPI {
    @POST("api/Alarm/GetList")
    fun getAlarmList(@Header("Authorization") auth: String, @Body alarmsearchrequest: AlarmSearchRequest): Observable<Ret<AlarmListData?>>

    @POST("api/Alarm/GetNoClearQuantity")
    fun getNoClearQuantity(@Header("Authorization") auth: String, @Body noclearsaerchrequest: NoClearSaerchRequest): Observable<Ret<NoClearData?>>
}