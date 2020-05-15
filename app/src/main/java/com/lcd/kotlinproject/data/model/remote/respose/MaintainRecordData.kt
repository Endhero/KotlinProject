package com.lcd.kotlinproject.data.model.remote.respose

data class MaintainRecordData (var ItemTotalCount : Int? = 0, var List: MutableList<Data>?){
    data class Data (var DurationTime: String?, var DurationHours: String?, var RunHours: String?, var MainTainType: String?, var Content: String?){
    }
}