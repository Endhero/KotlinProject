package com.lcd.kotlinproject.data.model.remote.request

import com.alibaba.fastjson.annotation.JSONField

data class RefreshRequest(@JSONField(name = "IMEI") var imei: String?, @JSONField(name = "RefreshToken") var refreshToken: String?) {
}