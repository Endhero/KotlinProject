package com.lcd.kotlinproject.data.model.remote.request

import com.alibaba.fastjson.annotation.JSONField

data class LoginRequest (@JSONField(name = "Telphone") var phone:String?,  @JSONField(name = "Code") var code: String?, @JSONField(name = "IMEI")  var imei: String?){
}