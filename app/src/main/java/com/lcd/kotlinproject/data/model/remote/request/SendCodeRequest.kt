package com.lcd.kotlinproject.data.model.remote.request

import com.alibaba.fastjson.annotation.JSONField

data class SendCodeRequest(@JSONField(name = "Telphone") var phone:String, @JSONField(name = "IMEI") var imei:String, @JSONField(name = "VerifyCodeType") var type:Int){
}