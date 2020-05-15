package com.lcd.kotlinproject.data.model.remote.respose

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import java.util.*

data class DeviceStatusData (
    @JSONField(name = "DeviceUID")
    var uid: String?,
    @JSONField(name = "DeviceName")
    var name: String? = null,
    @JSONField(name = "IsOnLine")
    var onLine: Boolean? = false,
    @JSONField(name = "UpdateTime")
    var updateTime: Date?,
    @JSONField(name = "FaultList")
    var faultList: List<String>?,
    @JSONField(name = "OxygenConcentration")
    var oxyConcentration: Float? = 0f,
    // 空压机压力
    @JSONField(name = "AirPressure")
    var airPressure: Float? = 0f,
    // 增压泵入口压力
    @JSONField(name = "InletPressure")
    var inletPressure: Float? = 0f,
    // 氧气储罐压力
    @JSONField(name = "OxygenPressure")
    var oxyPressure: Float? = 0f,
    // 产氧量
    @JSONField(name = "AverageFlowHour")
    var averageFlow: Float? = 0f,
    // 用氧量
    @JSONField(name = "TotalFlow")
    var totalFlow: Float? = 0f,
    // 空压机运行时长
    @JSONField(name = "AirDuration")
    var compressorDuration: Int? = 0,
    // 增压泵运行时长
    @JSONField(name = "BoosterDuration")
    var pumpDuration: Int? = 0,
    // 运行时长
    @JSONField(name = "AirRunHour")
    var runningHour: Int? = 0,
    // 空压机状态 1空压机正常，0故障
    @JSONField(name = "AirCompressor")
    var compressorState: Boolean? = false,
    // 增压泵状态 1增压泵正常，0故障
    @JSONField(name = "BoosterPump")
    var boosterPumpState: Boolean? = false,
    // 1紧急停止按钮正常，0紧急停止按钮被按下
    @JSONField(name = "StopButton")
    var stopButton: Boolean? = false,
    // 1空压机开，0关
    @JSONField(name = "AirCompressorSwitch")
    var compressorSwitch: Boolean? = false,
    // 1增压泵开，=0关
    @JSONField(name = "BoosterPumpSwitch")
    var boosterPumpSwitch: Boolean? = false,
    // 1报警器响，0不响
    @JSONField(name = "Alertor")
    var alert: Boolean? = false,
    // 制氧主机关，主机图为灰色，其他值为开，图片为黄色 0:关，1：开
    @JSONField(name = "OxygenHostState")
    var hostState: Boolean? = false,
    // 1表示制氧开机，0表示制氧机关机
    @JSONField(name = "OxygenState")
    var oxyState: Boolean? = false,
    // 1表示制氧处于手动状态，0表示自动状态
    @JSONField(name = "OxygenAutoState")
    var modeState: Boolean? = false,
    // 1表示压力到自动待机，0表示压力未到
    @JSONField(name = "OxygenAwaitState")
    var standbyState: Boolean? = false,
    // 复位值令 0:无，1：已复位
    @JSONField(name = "ResetVal")
    var resetVal: Boolean? = false,
    // 1表示“本机控制”，0表示“双机联动
    @JSONField(name = "ControlState")
    var controlState: Boolean? = false,
    // 急停按钮被按下
    @JSONField(name = "TheEmergencyStopButton")
    var emergencyStopButton: Int? = 0,
    // 空压机故障
    @JSONField(name = "AirCompressorFault")
    var compressorFault: Int? = 0,
    // 冷干机故障
    @JSONField(name = "ColdDrMachineFailure")
    var dryerFault: Int? = 0,
    // 增压泵故障
    @JSONField(name = "PumpFailure")
    var pumpFault: Int? = 0,
    // 压缩空气欠压时间>3分钟
    @JSONField(name = "CompressedAirTime")
    var compressorLowPressureTime: Int? = 0,
    // 压缩空气压力波动太大
    @JSONField(name = "CompressedAirPressureFluctuatesTooMuch")
    var fluctuatePressure: Int? = 0,
    // 氧气储存罐压力过低
    @JSONField(name = "OxygenTankPressureTooLow")
    var tankPressureLow: Int? = 0,
    // 氧气储存罐压力过高
    @JSONField(name = "OxygenTankPressureTooHigh")
    var tankPressureHigh: Int? = 0,
    // 增压泵进气压力偏低
    @JSONField(name = "ThePumpIntakePressureIsLow")
    var pumpPressureLow: Int? = 0,
    // 增压泵进气压力偏高
    @JSONField(name = "ThePumpIntakePressureIsHigh")
    var pumpPressureHigh: Int? = 0,
    // 氧气浓度偏低
    @JSONField(name = "LowOxygenConcentration")
    var oxyConcentrationLow: Int? = 0,
    // 氧气浓度传感器异常
    @JSONField(name = "AbnormalOxygenConcentrationSensor")
    var concentrationSensorError: Int? = 0) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uid)
        dest.writeString(name)
        dest.writeByte((if (onLine!!) 1 else 0).toByte())
        dest.writeLong(updateTime!!.time)
        dest.writeStringList(faultList)
        dest.writeFloat(oxyConcentration!!)
        dest.writeFloat(airPressure!!)
        dest.writeFloat(inletPressure!!)
        dest.writeFloat(oxyPressure!!)
        dest.writeFloat(averageFlow!!)
        dest.writeFloat(totalFlow!!)
        dest.writeInt(compressorDuration!!)
        dest.writeInt(pumpDuration!!)
        dest.writeInt(runningHour!!)
        dest.writeByte((if (compressorState!!) 1 else 0).toByte())
        dest.writeByte((if (boosterPumpState!!) 1 else 0).toByte())
        dest.writeByte((if (stopButton!!) 1 else 0).toByte())
        dest.writeByte((if (compressorSwitch!!) 1 else 0).toByte())
        dest.writeByte((if (boosterPumpSwitch!!) 1 else 0).toByte())
        dest.writeByte((if (alert!!) 1 else 0).toByte())
        dest.writeByte((if (hostState!!) 1 else 0).toByte())
        dest.writeByte((if (oxyState!!) 1 else 0).toByte())
        dest.writeByte((if (modeState!!) 1 else 0).toByte())
        dest.writeByte((if (standbyState!!) 1 else 0).toByte())
        dest.writeByte((if (resetVal!!) 1 else 0).toByte())
        dest.writeByte((if (controlState!!) 1 else 0).toByte())
        dest.writeInt(emergencyStopButton!!)
        dest.writeInt(compressorFault!!)
        dest.writeInt(dryerFault!!)
        dest.writeInt(pumpFault!!)
        dest.writeInt(compressorLowPressureTime!!)
        dest.writeInt(fluctuatePressure!!)
        dest.writeInt(tankPressureLow!!)
        dest.writeInt(tankPressureHigh!!)
        dest.writeInt(pumpPressureLow!!)
        dest.writeInt(pumpPressureHigh!!)
        dest.writeInt(oxyConcentrationLow!!)
        dest.writeInt(concentrationSensorError!!)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DeviceStatusData> {
        override fun createFromParcel(parcel: Parcel) = DeviceStatusData(
            parcel.readString(),
            parcel.readString(),parcel.readByte() > 0,
            Date(parcel.readLong()),
            parcel.createStringArrayList(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readByte() > 0,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun newArray(size: Int): Array<DeviceStatusData?> {
            return arrayOfNulls(size)
        }
    }
}