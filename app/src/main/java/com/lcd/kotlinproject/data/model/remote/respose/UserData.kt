package com.lcd.kotlinproject.data.model.remote.respose

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField

data class UserData (
    @JSONField(name = "UID")
    var id: String? = null,
    @JSONField(name = "Name")
    var name: String? = null,
    @JSONField(name = "Mobile")
    var mobile: String? = null,
    // 1:通知 0:不通知
    @JSONField(name = "IsNotice")
    var notification: Int = 0) : Parcelable{
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(mobile)
        parcel.writeInt(notification)
    }

    override fun describeContents() = 0

    fun isNotificationOn() = notification == 1

    fun setNotification(on: Boolean) {
        notification = if (on) 1 else 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readInt())
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}