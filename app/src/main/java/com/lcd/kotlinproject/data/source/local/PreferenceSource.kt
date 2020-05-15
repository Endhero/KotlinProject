package com.lcd.kotlinproject.data.source.local

import com.lcd.kotlinproject.CentralOGManagerApplication
import com.yuwell.androidbase.tool.SharedPreferencesUtil
import java.util.*

class PreferenceSource {
    private var token: String? = null
    private var appid: String? = null

    fun getAppId(): String {
        if (appid.isNullOrEmpty()) {
            appid = SharedPreferencesUtil.getSharedPreferences(getContext(), "appid", "")
        }

        if (appid.isNullOrEmpty()) {
            appid = UUID.randomUUID().toString().replace("-".toRegex(), "")
            SharedPreferencesUtil.setEditor(getContext(), "appid", appid)
        }

        return appid!!
    }

    fun getAuthorization() = "Bearer " + getToken()

    fun getToken(): String? {
        if (token.isNullOrEmpty()) {
            token = SharedPreferencesUtil.getSharedPreferences(getContext(), "t", "")
        }

        return token
    }

    fun setLoginInfo(token: String?, refreshToken: String?, expireTime: Date?) {
        setToken(token)
        setRefreshToken(refreshToken)
        setExpireTime(expireTime)
    }

    fun setToken(token: String?) {
        this.token =  token
        SharedPreferencesUtil.setEditor(getContext(), "t", token ?: "")
    }

    fun getRefreshToken() = SharedPreferencesUtil.getSharedPreferences(getContext(), "rt", "")

    private fun setRefreshToken(token: String?) {
        SharedPreferencesUtil.setEditor(getContext(), "rt", token ?: "")
    }

    fun getExpireTime(): Date? {
        val time = SharedPreferencesUtil.getSharedPreferences(
            getContext(),
            "expire",
            System.currentTimeMillis()
        )
        return Date(time)
    }

    fun setLoginUser(phone: String?) {
        SharedPreferencesUtil.setEditor(getContext(), "user", phone)
    }

    fun getLoginUser() = SharedPreferencesUtil.getSharedPreferences(getContext(), "user", "")

    private fun setExpireTime(time: Date?) {
        SharedPreferencesUtil.setEditor(
            getContext(), "expire", time?.time ?: System.currentTimeMillis()
        )
    }

    private fun getContext() = CentralOGManagerApplication.getInstance()
}