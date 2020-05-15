package com.lcd.kotlinproject

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import cn.jpush.android.api.JPushInterface
import com.ezvizuikit.open.EZUIKit
import com.lcd.kotlinproject.Const.Companion.BUGLY_APP_ID
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.videogo.openapi.BuildConfig
import com.videogo.openapi.EZOpenSDK

class CentralOGManagerApplication : Application() {
    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, _: RefreshLayout? ->
            ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, _: RefreshLayout? ->
            ClassicsFooter(context).setDrawableSize(20f)
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (isMainProcess(this)) {
            EZOpenSDK.showSDKLog(BuildConfig.DEBUG)
            EZOpenSDK.enableP2P(true)
            EZOpenSDK.initLib(this, Const.EZ_APP_KEY)
            EZUIKit.setDebug(BuildConfig.DEBUG)
            EZUIKit.initWithAppKey(this, Const.EZ_APP_KEY)
            Beta.autoCheckUpgrade = false
            Bugly.init(this, BUGLY_APP_ID, BuildConfig.DEBUG)
            JPushInterface.init(this)
            JPushInterface.setDebugMode(BuildConfig.DEBUG)
        }
    }

    private fun isMainProcess(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = context.packageName
        val myPid = Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }

        return false
    }

    companion object{
        private var instance : CentralOGManagerApplication? = null

        fun getInstance() = instance!!
    }
}