package com.lcd.kotlinproject.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.lcd.kotlinproject.CentralOGManagerApplication
import com.lcd.kotlinproject.Const
import java.io.File
import java.io.FileOutputStream

object PhotoUtil {
    const val PHOTO_SAVE_SUCCESS = 0
    const val PHOTO_SAVE_FAIL = 1

    fun savePhotoToGallery(strFilePath: String?, bitmap: Bitmap): Int {
        val file = File(strFilePath)
        val fileParent = file.parentFile

        if (fileParent == null || !fileParent.exists() || fileParent.isFile) {
            File(Const.CAPTRUE_SAVE_PATH).mkdirs()
        }

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                MediaStore.Images.Media.insertImage(CentralOGManagerApplication.getInstance().contentResolver, file.absolutePath, file.name, null)
                CentralOGManagerApplication.getInstance().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")))
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return PHOTO_SAVE_FAIL
        }

        return PHOTO_SAVE_SUCCESS
    }
}