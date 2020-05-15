package com.lcd.kotlinproject.view.impl.base

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.lcd.base.exception.GlobalError
import com.lcd.kotlinproject.BuildConfig
import com.lcd.kotlinproject.view.MainActivity
import com.yuwell.androidbase.tool.ResourceUtil
import com.yuwell.androidbase.tool.ToastUtil
import retrofit2.HttpException

/**
 * Created by Chen on 2019/11/28.
 */
open class ErrorObserver(private val context: Context) : Observer<GlobalError> {
    private val toastUtil: ToastUtil = ToastUtil(context)

    override fun onChanged(error: GlobalError) {
        val id = ResourceUtil.getStringId(context, "error_" + error.type)
        val commonTip = if (id > 0) context.getString(id) else "未知错误类型"
        val message: String? = error.throwable.message
        val builder = StringBuilder()

        if (error.type === GlobalError.ERROR_RET) {
            if (!message.isNullOrBlank() && BuildConfig.DEBUG) {
                builder.append(message)
            } else {
                builder.append(commonTip)
            }
        } else {
            builder.append(commonTip)

            if (BuildConfig.DEBUG && !message.isNullOrBlank()) {
                builder.append(": ").append(message)
            }

            if (error.type === GlobalError.ERROR_SERVER && error.throwable is HttpException) {
                val he = error.throwable as HttpException
                if (he.code() == 401) {
                    MainActivity.logoff(context)
                    builder.append(",").append("登录信息过期，请重新登录")
                }
            }
        }

        toastUtil.showToast(builder.toString())
    }

}