package com.lcd.kotlinproject.data.model.remote.respose

import com.alibaba.fastjson.annotation.JSONField

data class DeviceData (@JSONField(name = "UID") var id: String?, @JSONField(name = "DeviceName") var name: String?, @JSONField(name = "FaultCount") var errorCount: Int?){
}