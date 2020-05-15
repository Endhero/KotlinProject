package com.lcd.kotlinproject.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.videogo.widget.CheckTextButton

/**
 * Created by liuchangda 2020/ 03/ 20
 */
class ScreenOrientationHelper(private val mActivity: Activity, button: CheckTextButton?) : SensorEventListener {
    private val mButton: CheckTextButton? = button
    private var mOriginOrientation = 0
    private var mPortraitOrLandscape: Boolean? = null
    private val mSensorManager: SensorManager = mActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var mSensors: Array<Sensor?>? = arrayOfNulls<Sensor>(2)
    private var mAccelerometerValues = FloatArray(3)
    private var mMagneticFieldValues = FloatArray(3)

    fun enableSensorOrientation() {
        if (mSensors == null) {
            mOriginOrientation = mActivity.requestedOrientation
            mSensors!![0] = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            mSensors!![1] = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            mSensorManager.registerListener(this, mSensors!![0], SensorManager.SENSOR_DELAY_NORMAL)
            mSensorManager.registerListener(this, mSensors!![1], SensorManager.SENSOR_DELAY_NORMAL)
        }

        mButton?.isEnabled = true
    }

    @JvmOverloads
    fun disableSensorOrientation(reset: Boolean = true) {
        if (mSensors != null) {
            mSensorManager.unregisterListener(this, mSensors!![0])
            mSensorManager.unregisterListener(this, mSensors!![1])
            mSensors = null

            if (reset) {
                mActivity.requestedOrientation = mOriginOrientation
            }
        }

        mButton?.isEnabled = false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun landscape() {
        mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setButtonChecked(true)
        mPortraitOrLandscape = false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun portrait() {
        mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setButtonChecked(false)
        mPortraitOrLandscape = true
    }

    fun setButtonChecked(checked: Boolean) {
        mButton?.isChecked = checked
    }

    fun postOnStart() {
        mSensors?.let {
            mSensorManager.registerListener(this, mSensors!![0], SensorManager.SENSOR_DELAY_NORMAL)
            mSensorManager.registerListener(this, mSensors!![1], SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun postOnStop() {
        mSensors?.let {
            mSensorManager.unregisterListener(this, mSensors!![0])
            mSensorManager.unregisterListener(this, mSensors!![1])
        }
    }

    private fun calculateOrientation() {
        val values = FloatArray(3)
        val R = FloatArray(9)

        SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagneticFieldValues)
        SensorManager.getOrientation(R, values)

        mSensors?.let {
            if (mSensors!![1] == null) calculateByAccelerometer(mAccelerometerValues) else calculateByOrientation(
                values
            )
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun calculateByAccelerometer(values: FloatArray) {
        val orientation = mActivity.requestedOrientation
        if (-2f < values[1] && values[1] <= 2f && values[0] < 0) { // 向左
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && (mPortraitOrLandscape == null || !mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                setButtonChecked(true)
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (4f < values[1] && values[1] < 10f) { // 正南
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && (mPortraitOrLandscape == null || mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                setButtonChecked(false)
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (-10f < values[1] && values[1] < -4f) { // 正北
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && (mPortraitOrLandscape == null || mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                setButtonChecked(false)
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (-2f < values[1] && values[1] <= 2f && values[0] > 0) { // 向右
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE && (mPortraitOrLandscape == null || !mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                setButtonChecked(true)
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape!!) mPortraitOrLandscape = null
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun calculateByOrientation(values: FloatArray) {
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
        values[1] = Math.toDegrees(values[1].toDouble()).toFloat()
        values[2] = Math.toDegrees(values[2].toDouble()).toFloat()

        val orientation = mActivity.requestedOrientation

        if (-10.0f < values[1] && values[1] <= 10f && values[2] < -40f) { // 向左
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && (mPortraitOrLandscape == null || !mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                setButtonChecked(true)
            }
            if (mPortraitOrLandscape != null && !mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (40.0f < values[1] && values[1] < 90.0f) { // 正南
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && (mPortraitOrLandscape == null || mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                setButtonChecked(false)
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (-90.0f < values[1] && values[1] < -40.0f) { // 正北
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && (mPortraitOrLandscape == null || mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                setButtonChecked(false)
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape!!) mPortraitOrLandscape = null
        } else if (-10.0f < values[1] && values[1] <= 10f && values[2] > 40f) { // 向右
            if (orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE && (mPortraitOrLandscape == null || !mPortraitOrLandscape!!)) {
                mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                setButtonChecked(true)
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape!!) mPortraitOrLandscape = null
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> mMagneticFieldValues = event.values
            Sensor.TYPE_ACCELEROMETER -> mAccelerometerValues = event.values
            else -> {
            }
        }

        calculateOrientation()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    init {
        mButton?.isEnabled = false
        mButton?.setToggleEnable(false)
        mButton?.setOnClickListener {
            mSensors?.let {if (mButton.isChecked) portrait() else landscape()}
        }
    }
}