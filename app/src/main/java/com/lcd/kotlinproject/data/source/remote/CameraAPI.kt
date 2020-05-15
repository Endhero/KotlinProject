package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.respose.CameraListData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface CameraAPI {
    @GET("api/Camera/GetList")
    fun getCameraList(@Header("Authorization") auth: String?): Observable<Ret<CameraListData?>>
}