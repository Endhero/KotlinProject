package com.lcd.kotlinproject.data.source

import android.content.Context
import com.lcd.kotlinproject.data.source.local.PreferenceSource
import com.lcd.kotlinproject.data.source.remote.CameraAPI
import com.lcd.kotlinproject.utils.http.ServiceGenerator

class CameraRepository(context: Context) {
    private var api = ServiceGenerator.createService(CameraAPI::class.java)
    private var preferenceSource = PreferenceSource()

    fun getCameraList() = api.getCameraList(preferenceSource.getAuthorization())
}