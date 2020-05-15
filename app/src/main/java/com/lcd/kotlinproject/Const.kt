package com.lcd.kotlinproject

class Const {
    companion object{
         val CAPTRUE_SAVE_PATH: String = CentralOGManagerApplication.getInstance().cacheDir.absolutePath + "/captrue_save"
         val EZ_APP_KEY = "45293e0978d84cbcbdb3ecfe5e92d3be"
         val BUGLY_APP_ID = "ad79a07b95"
    }
}