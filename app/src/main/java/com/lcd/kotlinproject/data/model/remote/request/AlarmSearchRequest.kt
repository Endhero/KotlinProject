package com.lcd.kotlinproject.data.model.remote.request

data class AlarmSearchRequest(var StartDate: String?, var DeviceUID: String?, var IsClear: Int? = 0, var Page: Int? = 0, var PageSize: Int? = 0) {
}