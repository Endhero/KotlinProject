package com.lcd.kotlinproject.data.model.remote.respose

import java.io.Serializable

data class JobListData(var ItemTotalCount: Int? = 0, var List: MutableList<Data>?) : Serializable{
    data class Data(  var WorkOrderUID: String?,
                      var WorkOrderNum: String?,
                      var OrderType: String?,
                      var DeviceName: String?,
                      var DelTime: String?,
                      var Content: String?,
                      var Handler: String?,
                      var FilePath: String?,
                      var FileHead: String?) : Serializable {
    }
}