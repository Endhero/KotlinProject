package com.lcd.kotlinproject.data.model.remote.respose

import java.io.Serializable

data class CameraData(var CameraID: String?, var CameraName: String?, var ChannelNo: Int = 0, var EZurl: String?, var EZHDurl: String?, var EncryPwd: String?, var IsOnline: Int = 0): Serializable{
}