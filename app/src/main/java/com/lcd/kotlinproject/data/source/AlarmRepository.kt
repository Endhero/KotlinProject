package com.lcd.kotlinproject.data.source

import android.content.Context
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.AlarmSearchRequest
import com.lcd.kotlinproject.data.model.remote.request.NoClearSaerchRequest
import com.lcd.kotlinproject.data.model.remote.respose.AlarmListData
import com.lcd.kotlinproject.data.model.remote.respose.NoClearData
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.AlarmAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator
import io.reactivex.Observable

class AlarmRepository (context: Context) {
    private var api: AlarmAPI = ServiceGenerator.createService(AlarmAPI::class.java)
    private var preferenceSource: PreferenceSource = PreferenceSource()

    fun getAlarmList(alarmsearchrequest: AlarmSearchRequest): Observable<Ret<AlarmListData?>> = api.getAlarmList(preferenceSource.getAuthorization(), alarmsearchrequest)

    fun getNoClearQuantity(noreadsaerchrequest: NoClearSaerchRequest): Observable<Ret<NoClearData?>> = api.getNoClearQuantity(preferenceSource.getAuthorization(), noreadsaerchrequest)
}