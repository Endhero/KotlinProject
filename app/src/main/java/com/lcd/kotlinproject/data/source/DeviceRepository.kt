package com.lcd.kotlinproject.data.source

import android.content.Context
import android.util.ArrayMap
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.data.model.remote.request.ControlCommand
import com.lcd.kotlinproject.data.model.remote.respose.DeviceData
import com.lcd.kotlinproject.data.model.remote.respose.DeviceStatusData
import com.lcd.kotlinproject.data.source.local.MemorySource
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.DeviceAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator
import io.reactivex.Observable
import java.util.*
import kotlin.math.abs

class DeviceRepository(context: Context){
    private val memorySource: MemorySource = MemorySource()
    private val preferenceSource: PreferenceSource = PreferenceSource()
    private val deviceAPI: DeviceAPI = ServiceGenerator.createService(DeviceAPI::class.java)

    companion object{
        var statusDataList: MutableList<DeviceStatusData?> = ArrayList<DeviceStatusData?>()
        var lastUpdate: MutableMap<String, Long> = ArrayMap()
    }

    fun getDeviceList(force: Boolean): Observable<Ret<List<DeviceData?>>>{
        val deviceDataList = memorySource["device_unit"] as List<DeviceData>?

        if (deviceDataList != null && !force){
            val ret: Ret<List<DeviceData?>> = Ret.newRet(true)
            ret.data = deviceDataList
            return Observable.just<Ret<List<DeviceData?>>>(ret)
        }

        return deviceAPI.getDeviceUnit(preferenceSource.getAuthorization())
            .map {
                ret->

                if (ret.success){
                    memorySource.put("device_unit", ret.data)
                }

                ret
            }
    }

    fun getDeviceStatusList(): Observable<Ret<List<DeviceStatusData?>>> {
        return deviceAPI.getDeviceState(preferenceSource.getAuthorization())
            .map {
                listRet ->
                    if (listRet.success) {
                        statusDataList.clear()
                        statusDataList.addAll(listRet.data)
                        for (dsd in listRet.data) {
                            dsd?.uid?.let {lastUpdate[it] = System.currentTimeMillis()};
                        }
                    }

                listRet
            }
    }

    fun forceGetDeviceStatus(id: String?): Observable<Ret<DeviceStatusData?>>{
        return getDeviceStatusList()
            .map {
                listRet ->
                    val retData: Ret<DeviceStatusData?> = Ret.newRet(false)

                    if (listRet.success) {
                        for (data in listRet.data) {
                            data?.uid?.let {
                                lastUpdate[it] = System.currentTimeMillis()

                                if (data.uid == id) {
                                    retData.success = true
                                    retData.data = data
                                }
                            }
                        }
                    } else {
                        retData.code = listRet.code
                        retData.msg = listRet.msg
                    }

                retData
        }
    }

    fun getDeviceStatus(id: String): Observable<Ret<DeviceStatusData?>> {
        val time: Long = lastUpdate[id] ?: 0

        if (abs(System.currentTimeMillis() - time) < 60 * 1000) {
            if (statusDataList.size > 0) {
                for (data in statusDataList) {
                    data?.uid?.let {
                        if (data.uid == id) {
                            val retData: Ret<DeviceStatusData?> = Ret.newRet(true)
                            retData.data = data
                            return Observable.just(retData)
                        }
                    }
                }
            }
        }

        return forceGetDeviceStatus(id)
    }

    fun sendCtrlCommand(command: ControlCommand): Observable<Ret<String?>> {
        return deviceAPI.controlDevice(preferenceSource.getAuthorization(), command)
    }
}