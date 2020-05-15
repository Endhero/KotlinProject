package com.lcd.kotlinproject.data.source

import android.content.Context
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.MaintainAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator

class MaintainRepository(context: Context) {
    private var preferenceSource: PreferenceSource = PreferenceSource()
    private var maintainAPI: MaintainAPI = ServiceGenerator.createService(MaintainAPI::class.java)

    fun getMainTainPlan(strOrgUid: String) =  maintainAPI.getMainTainPlan(preferenceSource.getAuthorization(), strOrgUid)

    fun getMainTainInfo(strOrgUid: String, nPageNum: Int, strDeviceUID: String, nEquipmentType: Int) = maintainAPI.getMainTainInfo(preferenceSource.getAuthorization(), strOrgUid, nPageNum, strDeviceUID, nEquipmentType)

    fun getWorkOrder(strOrgUid: String, nPageNum: Int) = maintainAPI.getWorkOrder(preferenceSource.getAuthorization(), strOrgUid, nPageNum)
}