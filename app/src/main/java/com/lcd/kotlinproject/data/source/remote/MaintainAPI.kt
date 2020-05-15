package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.respose.JobListData
import com.lcd.kotlinproject.data.model.remote.respose.MaintainData
import com.lcd.kotlinproject.data.model.remote.respose.MaintainRecordData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MaintainAPI {
    @GET("api/MainTain/GetMainTainPlan")
    fun getMainTainPlan(@Header("Authorization") auth: String?, @Query("orgUID") strOrgUid: String): Observable<Ret<List<MaintainData?>>>

    @GET("api/MainTain/GetMainTainInfo")
    fun getMainTainInfo(@Header("Authorization") auth: String, @Query("orgUID") strOrgUid: String, @Query("pageNum") nPageNum: Int,
                        @Query("deviceUID") strDeviceUID: String, @Query("equipmentType") nEquipmentType: Int): Observable<Ret<MaintainRecordData?>>

    @GET("api/MainTain/GetWorkOrder")
    fun getWorkOrder(@Header("Authorization") auth: String, @Query("orgUID") strOrgUid: String, @Query("pageNum") nPageNum: Int): Observable<Ret<JobListData?>>
}