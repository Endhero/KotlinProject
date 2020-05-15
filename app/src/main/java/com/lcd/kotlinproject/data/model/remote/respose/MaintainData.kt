package com.lcd.kotlinproject.data.model.remote.respose

import java.io.Serializable

data class MaintainData(var OrgUID: String?, var OrgName: String?, var DeviceUID: String?, var DeviceName: String?, var EquipmentName: String?,
                        var EquipmentType: String?, var Content: String?, var DurationTime: String?, var SurplusHours: String?,
                        var MainTainType: String?) : Serializable{
}