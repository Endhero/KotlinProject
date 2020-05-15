package com.lcd.kotlinproject.data.model.remote.request

import com.alibaba.fastjson.annotation.JSONField

data class ControlCommand (@JSONField(name = "TelCode")  var code: String?, @JSONField(name = "OperationCode") var command: String?,
                           @JSONField(name = "DeviceUID") var deviceId: String?){
}