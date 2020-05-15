package com.lcd.kotlinproject.data.source.remote

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.respose.OrgData
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import io.reactivex.Observable
import retrofit2.http.*

interface UserAPI {
    @GET("api/User/GetBaseInfo")
    fun getOrgData(@Header("Authorization") auth: String): Observable<Ret<OrgData?>>

    @GET("api/User/GetUserList")
    fun getUserList(@Header("Authorization") auth: String): Observable<Ret<List<UserData?>>>

    @POST("api/User/AddUser")
    fun addUser(@Header("Authorization") auth: String, @Body user: UserData): Observable<Ret<Void>>

    @POST("api/User/DeleteUser")
    fun deleteUser(@Header("Authorization") auth: String, @Query("uid") uid: String): Observable<Ret<Void>>

    @POST("api/User/UpdateUser")
    fun updateUser(@Header("Authorization") auth: String, @Body User: UserData): Observable<Ret<Void>>

    @GET("api/User/GetLoginInfo")
    fun getLoginUser(@Header("Authorization") auth: String): Observable<Ret<UserData?>>
}