package com.lcd.kotlinproject.data.model.remote.respose

import com.alibaba.fastjson.annotation.JSONField

data class OrgData(@JSONField(name = "OrgUID") var id: String?, @JSONField(name = "OrgName") var name: String?, @JSONField(name = "Address") var address: String?,
                   @JSONField(name = "ProvinceName") var province: String?, @JSONField(name = "CityName") var city: String?){
}