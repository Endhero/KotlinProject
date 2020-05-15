package com.lcd.kotlinproject.data.model.remote.respose

import com.alibaba.fastjson.annotation.JSONField
import java.util.*

data class LoginData(@JSONField(name = "AccessToken") var accessToken: String?,  @JSONField(name = "Expires") var expires: Date?, @JSONField(name = "RefreshToken") var refreshToken: String?) {
}