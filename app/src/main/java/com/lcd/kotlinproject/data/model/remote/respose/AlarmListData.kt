package com.lcd.kotlinproject.data.model.remote.respose

data class AlarmListData(var List: MutableList<AlarmData>?, var ItemTotalCount: Int? = 0) {
    data class AlarmData (var UID: String?,   var DeviceName: String?, var CreateTime: String?, var ExceptionCode: String?,
                          var Description: String?, var ExceptionGrade: Int? = 0, var ClearTime: String?, var IsRead:Int = 0){
    }
}