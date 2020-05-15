package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.ControlCommand
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.data.model.remote.respose.DeviceStatusData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DeviceAPI {
    @GET("api/Device/GetInfo")
    fun getDeviceState(@Header("Authorization") auth: String): Observable<Ret<List<DeviceStatusData?>>>

    @GET("api/Device/GetDeviceUnit")
    fun getDeviceUnit(@Header("Authorization") auth: String): Observable<Ret<List<DeviceData?>>>

    @POST("api/Device/CommandArrive")
    fun controlDevice(@Header("Authorization") auth: String, @Body command: ControlCommand): Observable<Ret<String?>>
}