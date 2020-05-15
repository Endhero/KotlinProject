package com.lcd.kotlinproject.data.model.remote.respose

import com.alibaba.fastjson.annotation.JSONField

data class CameraListData (var AccessToken: String?, var ExpirationTime: String?, @JSONField(name = "detailList") var CameraList: MutableList<CameraData>?){
}