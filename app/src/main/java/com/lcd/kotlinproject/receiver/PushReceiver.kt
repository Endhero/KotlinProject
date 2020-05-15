package com.lcd.kotlinproject.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.impl.alarm.Alarm
import com.lcd.kotlinproject.view.impl.maintain.Maintain

class PushReceiver: JPushMessageReceiver() {
    companion object{
        private const val TAG: String = "PushReceiver"
    }

    override fun onMessage(context: Context, customMessage: CustomMessage) {
        super.onMessage(context, customMessage)

        Log.d(TAG, "onMessage: " + customMessage.message)
    }

    override fun onNotifyMessageArrived(context: Context, notificationMessage: NotificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage)

        Log.d(TAG, "onNotifyMessageArrived: $notificationMessage")
    }

    override fun onNotifyMessageOpened(context: Context, notificationMessage: NotificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage)

        Log.d(TAG,"onNotifyMessageOpened: $notificationMessage")

        var intent: Intent? = null

        if (notificationMessage.notificationTitle.isNullOrBlank()) {
            when(notificationMessage.notificationTitle) {
                context.getString(R.string.maintain_message)-> intent = Intent(context, Maintain::class.java)
                context.getString(R.string.alarm_message)-> intent = Intent(context, Alarm::class.java)
                context.getString(R.string.clear_message)-> {
                    intent = Intent(context, Alarm::class.java)
                    intent.putExtra("selected", 1)
                }
            }
        }

        intent?.let {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}