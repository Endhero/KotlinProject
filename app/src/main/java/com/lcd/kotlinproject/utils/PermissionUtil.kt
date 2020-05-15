package com.lcd.kotlinproject.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    const val REQUEST_STORAGE = 1
    const val REQUEST_AUDIO = 2

    inline fun <reified T: Activity> applyPermissionStorage(activity: T) = if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE)

            false
        } else true

    inline fun <reified T: Activity>applyPermissionAudio(activity: T) = if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO)

            false
        } else true
}